package arrow.refinement

import arrow.refinement.booleans.And
import arrow.refinement.booleans.Not
import arrow.refinement.booleans.Or

typealias Constraints = List<Pair<Boolean, String>>

fun ensure(vararg constraints: Pair<Boolean, String>) : Constraints =
  constraints.toList()

/**
 * This is a compile time only function for compatibility with varargs and to cope with the
 * equality mismatch between Pair[] and Object[]
 * @suppress
 */
fun ensureA(vararg constraints: Any) : Constraints =
  constraints.filterIsInstance<Pair<Boolean, String>>() +
    constraints.filterIsInstance<Constraints>().flatten()

inline fun Constraints.allValid(): Boolean =
  all { it.first }

inline fun renderMessages(results: Constraints): String =
  results.filter { !it.first }.joinToString { it.second }

fun require(constraints: Constraints): Unit {
  if (constraints.allValid()) Unit
  else throw java.lang.IllegalArgumentException(renderMessages(constraints))
}

inline fun <A> constraints(refined: Refined<A, *>, value: A): Constraints =
  refined.constraints(value)

abstract class Refined<A, B>(
  inline val f: (A) -> B,
  inline val constraints: (A) -> Constraints
) {

  constructor(f: (A) -> B, vararg predicates: Refined<A, *>) : this(f, { a: A ->
    when {
      predicates.isEmpty() -> emptyList()
      predicates.size == 1 -> predicates[0].constraints(a)
      else -> predicates.reduce { l, r -> l + r }.constraints(a)
    }
  })

  inline operator fun invoke(value: A): B {
    val results = constraints(value)
    return if (results.allValid()) f(value)
    else throw IllegalArgumentException(renderMessages(results))
  }

  inline fun require(value: A): B =
    invoke(value)

  inline operator fun not(): Refined<A, B> =
    Not(this)

  inline fun orNull(value: A): B? =
    if (constraints(value).allValid()) f(value)
    else null

  inline fun isValid(value: A): Boolean =
    constraints(value).allValid()

  inline fun <C> fold(value: A, ifInvalid: (Constraints) -> C, ifValid: (B) -> C): C {
    val results = constraints(value)
    return if (results.allValid()) ifValid(f(value))
    else {
      val failedConstrains = results.filterNot { it.first }
      ifInvalid(failedConstrains)
    }
  }

  inline operator fun <C> plus(other: Refined<A, C>): Refined<A, C> =
    And(this, other)

}

inline infix fun <A, B> Refined<A, *>.or(other: Refined<A, B>): Refined<A, B> =
  Or(this, other)

inline infix fun <A, B> Refined<A, *>.and(other: Refined<A, B>): Refined<A, B> =
  this + other
