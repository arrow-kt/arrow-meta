package arrow.meta.continuations

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.experimental.ExperimentalTypeInference

/**
 * Data class ContSeq that wraps a multi-shot control-flow aware function.
 * It allows for deferred & lazy execution, and passing it around for composition.
 *
 *
 */
interface ContSeq<out A> {
  operator fun iterator(): Iterator<A>

  /** Turns [ContSeq] into an in-memory representation as a [List] */
  fun toList(): List<A> = mutableListOf<A>().apply {
    this@ContSeq.forEach(::add)
  }

  /** Turns [ContSeq] into an in-memory representation as a [Set] */
  fun toSet(): Set<A> = mutableSetOf<A>().apply {
    this@ContSeq.forEach(::add)
  }

  /** Throws away the output of every emission */
  fun void(): ContSeq<Unit> = map { }

  /** Aborts all values, resulting in an empty [ContSeq] */
  fun dropAll(): ContSeq<Nothing> =
    andThen { } // Never yield a value
  // flatMap { empty() } // Return empty sequence for every value
  // andThen { abort() } // Abort every value

  /** Runs the sequence of computations, but ignore the output of every computation */
  fun drain(): Unit =
    forEach { }

  /** Smart-constructors */
  companion object {
    fun <A> empty(): ContSeq<A> = EMPTY

    operator fun <A> invoke(vararg aas: A): ContSeq<A> =
      ContSeq { yieldAll(aas.iterator()) }

    /**
     * Builds a [Sequence] lazily yielding values one by one.
     */
    @OptIn(ExperimentalTypeInference::class)
    operator fun <A> invoke(@BuilderInference block: suspend ContSeqSyntax<A>.() -> Unit): ContSeq<A> =
      object : ContSeq<A> {
        override fun iterator(): Iterator<A> = ContSeqBuilder<A>().apply {
          nextStep = block.createCoroutineUnintercepted(receiver = this, completion = this)
        }
      }
  }
}

/**
 * Alias for for-looping over all elements. Most efficient way to derive combinators.
 */
inline fun <A> ContSeq<A>.forEach(f: (A) -> Unit) {
  for (t in iterator()) f(t)
}

/**
 * Execute a list of [ContSeq] and gather the values.
 */
fun <A> List<ContSeq<A>>.sequence(): ContSeq<List<A>> =
  if (isEmpty()) {
    yieldOne { emptyList() }
  } else {
    this[0].zip(this.drop(1).sequence()) { x, xs ->
      listOf(x) + xs
    }
  }

/**
 * This covers most Functor hierarchy operations, since you can change DSLs.
 *
 * ```
 * ContSeq {
 *    yield(1)
 *    yield(2)
 *    yield(3)
 *    println("This to never be printed")
 *    repeat(nBranches) { yield(4) }
 *    yield(5)
 *   }
 *   .andThen { i -> yield(i + 1) } // map
 *   .andThen { i -> yieldAll(i + 1, i + 2) } // flatMap
 *   .andThen { i -> if(i % 2 == 0) yield(i) } //filterMap
 *   .andThen { i ->
 *       contSeq(1).forEach { b -> yield(i + b) }
 *   }                                             // zip
 *   // .andThen { abort() } // Abort every value
 *   .andThen { if(i == 3) abortRemaining() }
 *   .andThen { if(i == 2) repeatElements(2) }
 *   .toList() // empty list since we aborted all values
 * ```
 */
@OptIn(ExperimentalTypeInference::class)
inline fun <A, B> ContSeq<A>.andThen(@BuilderInference crossinline f: suspend ContSeqSyntax<B>.(A) -> Unit): ContSeq<B> =
  ContSeq {
    forEach { a -> f(a) }
  }

@OptIn(ExperimentalTypeInference::class)
inline fun <A> ContSeq<A>.andThenSideEffect(@BuilderInference crossinline f: (A) -> Unit): ContSeq<A> =
  andThen {
    f(it)
    yield(it)
  }

@OptIn(ExperimentalTypeInference::class)
inline fun <A, B> ContSeq<A>.map(@BuilderInference crossinline f: (A) -> B): ContSeq<B> =
  andThen { yield(f(it)) }

@OptIn(ExperimentalTypeInference::class)
inline fun <A, B> ContSeq<A>.flatMap(@BuilderInference crossinline f: (A) -> ContSeq<B>): ContSeq<B> =
  andThen { a ->
    f(a).forEach { b ->
      yield(b)
    }
  }

inline fun <A, B, C> ContSeq<A>.zip(fb: ContSeq<B>, crossinline f: (A, B) -> C): ContSeq<C> =
  andThen { a ->
    fb.forEach { b ->
      yield(f(a, b))
    }
  }

inline fun <A> yieldOne(crossinline x: () -> A): ContSeq<A> = ContSeq {
  yield(x())
}

/**
 * Yield a single `Unit` value. This is useful when
 * you want to signal that you want to keep the
 * computation going, but have no further information
 * to gather.
 */
suspend fun ContSeqSyntax<Unit>.goOn(): Unit = yield(Unit)

/**
 * Perform a side effect and ditch the return value.
 */
@OptIn(ExperimentalTypeInference::class)
inline fun sideEffect(@BuilderInference crossinline f: () -> Unit): ContSeq<Unit> = ContSeq {
  f()
  goOn()
}

val doNothing: ContSeq<Unit> = sideEffect { }

/**
 * Execute a side effect only when some condition holds.
 */
@OptIn(ExperimentalTypeInference::class)
inline fun doOnlyWhen(condition: Boolean, @BuilderInference crossinline f: () -> ContSeq<Unit>): ContSeq<Unit> =
  if (condition) f() else doNothing

/**
 * The scope for yielding values of a [ContSeq],
 * provides [yield] and [yieldAll] suspension functions.
 *
 * It also exposes [abort] which allows for interruption of the sequence,
 * meaning that when `abort` is encountered the `ContSeq` will be interrupted and stops yielding items.
 * However it's a **pure** interruption of the sequence, meaning it doesn't involve throwing exceptions or halting the program.
 * Other functions like `guard` or `ensureNotNull` are utility derivation of `abort`.
 */
@RestrictsSuspension
sealed interface ContSeqSyntax<in A> {
  suspend fun <A> abort(): A

  suspend fun yield(value: A)
  suspend fun yieldAll(iterator: Iterator<A>)
  suspend fun yieldAll(elements: Iterable<A>) {
    if (elements is Collection && elements.isEmpty()) return
    return yieldAll(elements.iterator())
  }

  suspend fun yieldAll(sequence: Sequence<A>) = yieldAll(sequence.iterator())
  suspend fun yieldAll(sequence: ContSeq<A>) = yieldAll(sequence.iterator())
}

suspend fun <A> ContSeqSyntax<A>.guard(condition: Boolean): Unit =
  if (condition) Unit else abort()

suspend fun <A> ContSeqSyntax<A>.ensure(condition: Boolean): Unit =
  guard(condition)

suspend fun <A : Any> ContSeqSyntax<A>.yieldNotNull(value: A?): Unit =
  yield(ensureNotNull(value))

@OptIn(ExperimentalContracts::class)
suspend fun <A : Any> ContSeqSyntax<A>.ensureNotNull(value: A?): A {
  contract { returns() implies (value != null) }
  return value ?: abort()
}

// Internal State Machinery
// The Builder's state:
// Starts in `State_NotReady` which means no values are available,
// This state should never be reach-able by the user since it only resides in this side when executing the lambda `suspend ContSeqSyntax<A>.() -> Unit`.
// It either goes to `State_Done` without any values emitted,
// or `yield` gets called while executing the lambda changing the state to `State_Ready` with any amount values emitted.
//
// When a value yielded and consumed, the state machine is in (`State_Ready`) and it will go back to `State_NotReady`.
// This will also allow the lambda `suspend ContSeqSyntax<A>.() -> Unit` until a next `yield` is found,
// or the lambda finishes either putting the state back to `State_Ready` or `State_Done`.
//
// So the state machine composes the `suspend function` from `yield` with the downstream logic,
// and it interleaves those composed functions by changing between `State_NotReady` and `State_Ready`.
//
// State_ManyReady & State_ManyNotReady do the same thing, but single there is a value inside [nextIterator] instead of [nextValue].
//
// When a developer mistake was made, then the mechanism will run into `State_Failed` and throw an exception.
private typealias State = Int

private const val State_NotReady: State = 0
private const val State_ManyNotReady: State = 1
private const val State_ManyReady: State = 2
private const val State_Ready: State = 3
private const val State_Done: State = 4
private const val State_Failed: State = 5

private val EMPTY: ContSeq<Nothing> = ContSeq { }

private class ContSeqBuilder<T> : ContSeqSyntax<T>, Iterator<T>, Continuation<Unit> {
  private var state = State_NotReady
  private var nextValue: T? = null
  private var nextIterator: Iterator<T>? = null
  var nextStep: Continuation<Unit>? = null

  override suspend fun <A> abort(): A {
    state = State_Done
    return suspendCoroutineUninterceptedOrReturn { COROUTINE_SUSPENDED }
  }

  override fun hasNext(): Boolean {
    while (true) {
      when (state) {
        State_NotReady -> {
        }
        State_ManyNotReady ->
          if (nextIterator!!.hasNext()) {
            state = State_ManyReady
            return true
          } else {
            nextIterator = null
          }
        State_Done -> return false
        State_Ready, State_ManyReady -> return true
        else -> throw exceptionalState()
      }

      state = State_Failed
      val step = nextStep!!
      nextStep = null
      step.resume(Unit)
    }
  }

  override fun next(): T = when (state) {
    State_NotReady, State_ManyNotReady -> nextNotReady()
    State_ManyReady -> {
      state = State_ManyNotReady
      nextIterator!!.next()
    }
    State_Ready -> {
      state = State_NotReady
      @Suppress("UNCHECKED_CAST")
      val result = nextValue as T
      nextValue = null
      result
    }
    else -> throw exceptionalState()
  }

  private fun nextNotReady(): T {
    if (!hasNext()) throw NoSuchElementException() else return next()
  }

  private fun exceptionalState(): Throwable = when (state) {
    State_Done -> NoSuchElementException()
    State_Failed -> IllegalStateException("Iterator has failed.")
    else -> IllegalStateException("Unexpected state of the iterator: $state")
  }

  override suspend fun yield(value: T) {
    nextValue = value
    state = State_Ready
    return suspendCoroutineUninterceptedOrReturn { c ->
      nextStep = c
      COROUTINE_SUSPENDED
    }
  }

  override suspend fun yieldAll(iterator: Iterator<T>) {
    if (!iterator.hasNext()) return
    nextIterator = iterator
    state = State_ManyReady
    return suspendCoroutineUninterceptedOrReturn { c ->
      nextStep = c
      COROUTINE_SUSPENDED
    }
  }

  override fun resumeWith(result: Result<Unit>) {
    result.getOrThrow()
    state = State_Done
  }

  override val context: CoroutineContext
    get() = EmptyCoroutineContext
}
