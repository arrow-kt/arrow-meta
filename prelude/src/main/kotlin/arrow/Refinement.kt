package arrow

import java.lang.IllegalArgumentException
import kotlin.reflect.KProperty

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Refinement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RefinedBy(val value: String)

inline fun require(vararg predicates: Pair<Boolean, String>): Unit {
  val failed = predicates.filterNot { (passed, _) -> passed }
  return if (failed.isEmpty()) Unit else {
    val msg = "Constraint failed: ${failed.joinToString { it.second }}"
    require(false) { msg }
  }
}

inline fun constraint(assertion: Boolean, msg: String): Pair<Boolean, String> =
  Pair(assertion, msg)

inline fun <A> require(vararg constrains: (A) -> A): (A) -> A = {
  var result: A? = null
  var t1: IllegalArgumentException? = null
  for (f in constrains) {
    try {
      result = f(it)
    } catch (t: IllegalArgumentException) {
      if (t1 != null) t1 = t1!! + t
      else t1 = t
    }
  }
  val t = t1
  result ?: if (t != null) throw t else it
}

operator fun IllegalArgumentException.plus(other: IllegalArgumentException): IllegalArgumentException =
  IllegalArgumentException("$message\n${other.message}", other)

typealias Constrains = Map<Boolean, String>

fun Constrains.allValid(): Boolean =
  all { it.key }

@JvmInline
value class Predicate<A>(val constrains: (A) -> Constrains) {

  inline operator fun invoke(n: A): A {
    val results = constrains(n)
    return if (results.allValid()) n
    else throw IllegalAccessException(renderMessages(results))
  }

  inline fun renderMessages(results: Constrains): String =
    results.entries.filter { !it.key }.joinToString { it.value }

  inline fun orNull(n: A): A? =
    if (constrains(n).allValid()) n
    else null

  inline operator fun plus(other: Predicate<A>): Predicate<A> =
    Predicate { constrains(it) + other.constrains(it) }

  inline operator fun not(): Predicate<A> =
    Predicate { constrains(it).map { it.key to "negated: ${it.value}" }.toMap() }

  inline infix fun and(other: Predicate<A>): Predicate<A> =
    this + other

  inline infix fun or(other: Predicate<A>): Predicate<A> =
    Predicate {
      val thisResult = constrains(it)
      val otherResult = other.constrains(it)
      val thisPassed = thisResult.allValid()
      val otherPassed = otherResult.allValid()
      if (thisPassed && !otherPassed) thisResult
      else if (!thisPassed && otherPassed) otherResult
      else thisResult + otherResult
    }

  inline operator fun getValue(a: A?, property: KProperty<*>): Predicate<A> = this
}


inline val Positive get() = Predicate<Int> { mapOf((it > 0) to "$it should be > 0") }
inline val Even get() = Predicate<Int> { mapOf((it % 2 == 0) to "$it should be an even number") }
inline val PositiveEven get() = Positive and Even
inline val PositiveOrEven get() = Positive or Even

fun main() {
  val n: Int = PositiveEven(1)
  println(n)
}