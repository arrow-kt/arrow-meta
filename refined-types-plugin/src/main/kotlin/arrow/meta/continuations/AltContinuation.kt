import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

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
fun computation(f: suspend Cont.() -> Unit): Unit {
    val cont = Continuation<Unit>(EmptyCoroutineContext) {
        it.fold({ }, { e -> throw e })
    }
    f.startCoroutine(Cont(cont), cont)
}

/**
 * Syntax class for [computation], which enables the DSL syntax inside the lambda block.
 * It implements control flow methods by having a reference to it's outer [Continuation],
 * which allows us to short-circuit by resuming to the parent instead of to our own scope.
 */
@RestrictsSuspension
class Cont(private val cont: Continuation<Unit>) : Continuation<Unit> by cont {

    suspend fun <A> abort(): A =
        suspendCoroutine { this@Cont.resume(Unit) }

    suspend fun guard(condition: Boolean): Unit =
        if (condition) Unit else abort()

    suspend fun ensure(condition: Boolean): Unit =
        guard(condition)

    @OptIn(ExperimentalContracts::class)
    suspend fun <A : Any> ensureNotNull(value: A?): A {
        contract {
            returns() implies (value != null)
        }

        return value ?: abort()
    }
}

fun main() {
    computation {
        (0..3).forEach {
            println("Hello, $it")
        }
    }

    computation {
        (0..3).forEach { n ->
            if (n >= 1) abort() else Unit
        }
    }

    sequence {
        yield(1)
        yield(2)
    }.map { n ->
        println("Hello $n")
    }.toList() // drain sequence
}
