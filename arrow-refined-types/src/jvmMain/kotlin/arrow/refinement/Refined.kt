package arrow.refinement

import arrow.refinement.booleans.And
import arrow.refinement.booleans.Not
import arrow.refinement.booleans.Or

typealias Constraints = Map<Boolean, String>

fun ensure(vararg constraints: Pair<Boolean, String>) : Map<Boolean, String> =
  mapOf(*constraints)

fun ensureA(vararg constraints: Any) : Map<Boolean, String> =
  constraints.filterIsInstance<Pair<Boolean, String>>().toMap() +
    constraints.filterIsInstance<Map<Boolean, String>>().fold(emptyMap(), Map<Boolean, String>::plus)

inline fun Constraints.allValid(): Boolean =
  all { it.key }

inline fun renderMessages(results: Constraints): String =
  results.entries.filter { !it.key }.joinToString { it.value }

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
    predicates.map { it.constraints(a) }.fold(emptyMap<Boolean, String>()) { acc, constrains ->
      acc + constrains
    }
  })

  inline operator fun invoke(value: A): B {
    val results = constraints(value)
    return if (results.allValid()) f(value)
    else throw IllegalArgumentException(renderMessages(results))
  }

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
      val failedConstrains = results.filterNot { it.key }
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
