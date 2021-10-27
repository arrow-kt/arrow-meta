@file:OptIn(ExperimentalTypeInference::class)

package arrow.meta.continuations

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
 * ContSeq is multi-shot control-flow streaming type.
 * It can emit 0..n elements, and it has ability to abort processing elements at any given time in
 * the stream without aborting the whole stream.
 *
 * It's lazy, and thus allows for passing it around for composition.
 *
 * It requires **explicit** running by using a terminal operator such as
 * [drain], [forEach], [toList] or [toSet].
 *
 * Besides that [ContSeq] exposes APIs that you mind find on any other `Monad`.
 */
sealed class ContSeq<out A> {

  abstract operator fun iterator(): Iterator<A>

  /**
   * Alias for for-looping over all elements. Most efficient way to derive combinators.
   */
  inline fun forEach(f: (A) -> Unit) {
    for (t in iterator()) f(t)
  }

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

  /** Runs the sequence of computations, but ignore the output of every computation */
  fun drain(): Unit = forEach { }

  /**
   * Maps every element in the sequence with `f`,
   * and allows for aborting the mapped value using [ContSyntax.abort] or [ContSyntax.ensure].
   */
  fun <B> map(f: suspend ContSyntax.(A) -> B): ContSeq<B> =
    ContSeq {
      // Weird hack for RestrictSuspension
      val f = f as suspend ContSeqSyntax<B>.(A) -> B
      forEach { a -> yield(f(a)) }
    }

  /** Side-effecting version of map */
  inline fun onEach(crossinline f: (A) -> Unit): ContSeq<A> = map { it.also(f) }

  fun <B> flatMap(f: (A) -> ContSeq<B>): ContSeq<B> =
    ContSeq {
      forEach { a -> yieldAll(f(a)) }
    }

  inline fun <B, C> zip(fb: ContSeq<B>, crossinline f: (A, B) -> C): ContSeq<C> =
    flatMap { a ->
      fb.map { b -> f(a, b) }
    }

  class VarArg<A>(private val aas: Array<out A>) : ContSeq<A>() {
    override fun iterator(): Iterator<A> = aas.iterator()
  }

  class Iterable<A>(private val aas: Iterable<A>) : ContSeq<A>() {
    override fun iterator(): Iterator<A> = aas.iterator()
  }

  class Builder<A>(private val block: suspend ContSeqSyntax<A>.() -> Unit) : ContSeq<A>() {
    override fun iterator(): Iterator<A> = ContSeqBuilder<A>().apply {
      nextStep = block.createCoroutineUnintercepted(receiver = this, completion = this)
    }
  }

  /** Smart-constructors */
  companion object {
    val unit: ContSeq<Unit> = ContSeq(Unit)

    val abort: ContSeq<Nothing> = ContSeq { abort() }

    fun <A> empty(): ContSeq<A> = EMPTY

    operator fun <A> invoke(aas: Iterable<A>): ContSeq<A> = Iterable(aas)

    operator fun <A> invoke(vararg aas: A): ContSeq<A> = VarArg(aas)

    /** Builds a [Sequence] lazily yielding values one by one. */
    operator fun <A> invoke(@BuilderInference block: suspend ContSeqSyntax<A>.() -> Unit): ContSeq<A> =
      Builder(block)
  }
}

/**
 * Smart constructor for single-shot [ContSeq] effect.
 */
inline fun <A> cont(crossinline f: () -> A): ContSeq<A> =
  ContSeq { yield(f()) }

/** Convert any [Iterable] into a [ContSeq] */
fun <A> Iterable<A>.asContSeq(): ContSeq<A> =
  ContSeq { yieldAll(this@asContSeq) }

/** Execute a list of [ContSeq] and gather the values. */
fun <A> List<ContSeq<A>>.sequence(): ContSeq<List<A>> =
  if (isEmpty()) ContSeq(emptyList()) else ContSeq(flatMap { it.toList() })

fun <A> List<ContSeq<A>>.nested(): ContSeq<List<A>> =
  if (isEmpty()) {
    cont { emptyList() }
  } else {
    first().flatMap { x ->
      drop(1).nested().map { xs ->
        listOf(x) + xs
      }
    }
  }

/** Execute a side effect only when some condition holds. */
inline fun doOnlyWhen(condition: Boolean, crossinline f: () -> ContSeq<Unit>): ContSeq<Unit> =
  if (condition) f() else ContSeq.unit

/** Execute a side effect only when some condition holds. */
inline fun <A> doOnlyWhen(condition: Boolean, value: A, crossinline f: () -> ContSeq<A>): ContSeq<A> =
  if (condition) f() else cont { value }

/** Execute a side effect only when some condition holds. */
inline fun <T, A> doOnlyWhenNotNull(thing: T?, value: A, crossinline f: (T) -> ContSeq<A>): ContSeq<A> {
  return if (thing != null) f(thing) else cont { value }
}

/**
 * [ContSyntax] exposes [abort] which allows for interruption of the computation,
 * meaning that when `abort` is encountered the `Cont` will be interrupted and will not yield a value.
 *
 * However it's a **pure** interruption of the computation, meaning it doesn't involve throwing exceptions or halting the program.
 * Other functions like `guard` or `ensureNotNull` are utility derivation of `abort`.
 */
sealed interface ContSyntax {
  suspend fun <A> abort(): A

  suspend fun ensure(condition: Boolean): Unit =
    if (condition) Unit else abort()
}

/**
 * The scope for yielding values of a [ContSeq],
 * provides [yield] and [yieldAll] suspension functions.
 */
@RestrictsSuspension
sealed interface ContSeqSyntax<in A> : ContSyntax {
  suspend fun yield(value: A)
  suspend fun yieldAll(iterator: Iterator<A>)
  suspend fun yieldAll(elements: Iterable<A>) {
    if (elements is Collection && elements.isEmpty()) return
    return yieldAll(elements.iterator())
  }

  suspend fun yieldAll(sequence: Sequence<A>) = yieldAll(sequence.iterator())
  suspend fun yieldAll(sequence: ContSeq<A>) = yieldAll(sequence.iterator())
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
