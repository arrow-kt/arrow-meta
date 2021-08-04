package arrow.meta.continuations

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

typealias SimpleComputation<A> = Computation<Unit, A>

class Computation<R, A>(val computation: suspend ContinuationScope<R>.() -> A) {
  fun forget(): Computation<R, Unit> = computation {
    this@Computation.bind()
    Unit
  }
}

fun <A> Computation<A, A>.run(): A {
  var value: Any? = EMPTY_VALUE
  val cont = Continuation<A>(EmptyCoroutineContext) {
    it.fold({ a ->
      value = a
    }, { e -> throw e })
  }
  computation.startCoroutine(ContinuationScope(cont), cont)
  return EMPTY_VALUE.unbox(value)
}

/**
 * `computation` block that allows for control flow through [Continuation] with pure values.
 *
 * A computation block always results in [Unit],
 *   we cannot short-circuit with `R` (unless if we use exceptions for short-circuiting with Result.failure(pureExceptionWithR))
 *   so you can recover `R` from the continuation callback below.
 *
 * This can also support concurrency and foreign suspension but this is currently disabled,
 * since the use-cases is to run pure-computations in the compiler APIs.
 *
 * Possible improvements:
 *    We can defer the computation, such that `computation` returns a wrapped version of `suspend Cont.() -> Unit`,
 *    this would allow further composition of computation by deferral of execution.
 *    Triggering the computation could then be don by a `run` method.
 */
fun <R, A> computation(f: suspend ContinuationScope<R>.() -> A): Computation<R, A> = Computation(f)

/**
 * Syntax class for [computation], which enables the DSL syntax inside the lambda block.
 * It implements control flow methods by having a reference to it's outer [Continuation],
 * which allows us to short-circuit by resuming to the parent instead of to our own scope.
 */
@RestrictsSuspension
class ContinuationScope<R>(private val cont: Continuation<R>) : Continuation<R> by cont {

  suspend fun <A> Computation<R, A>.bind(): A =
    computation.invoke(this@ContinuationScope)

  suspend fun <A> abort(r: R): A =
    suspendCoroutine { this@ContinuationScope.resume(r) }

  suspend fun <A, B> List<A>.contEach(f: ((a: A) -> Computation<R, B>)): List<B> =
      if (this@contEach.isEmpty()) emptyList()
      else {
        val b = f(this@contEach.first()).bind()
        val bs = this@contEach.drop(1).contEach(f)
        listOf(b) + bs
      }

  fun <A> reifyCont(f: ((x: A) -> R) -> R): Computation<R, A> = Computation { f } //TODO

  suspend fun <A> Int.contEach(f: (n: Int) -> Computation<R, A>): List<A> =
    List(this) { it }.contEach { n -> f(n) }
}

suspend fun ContinuationScope<Unit>.guard(condition: Boolean): Unit =
  if (condition) Unit else abort(Unit)

suspend fun ContinuationScope<Unit>.ensure(condition: Boolean): Unit =
  guard(condition)

@OptIn(ExperimentalContracts::class)
suspend fun <A : Any> ContinuationScope<Unit>.ensureNotNull(value: A?): A {
  contract {
    returns() implies (value != null)
  }

  return value ?: abort(Unit)
}

// Hack for more efficient nested nulls
@Suppress("ClassName")
internal object EMPTY_VALUE {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <T> unbox(value: Any?): T =
    if (value === this) null as T else value as T
}
