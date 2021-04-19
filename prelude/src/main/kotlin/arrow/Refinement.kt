package arrow

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Refinement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RefinedBy(val value: String)

inline fun require(predicate: Boolean, msg: String): Unit =
  require(predicate) { msg }

inline fun require(vararg predicates: Pair<Boolean, String>): Unit {
  val failed = predicates.filterNot { (passed, _) -> passed }
  return if (failed.isEmpty()) Unit else {
    val msg = "Constraint failed: ${failed.joinToString { it.second }}"
    require(false) { msg }
  }
}

inline fun Positive(n: Int): Int {
  require(n > 0, "expected $n to be > 0")
  require(n > 1, "expected $n to be > 1")
  return n
}

inline fun constraint(assertion: Boolean, msg: String): Pair<Boolean, String> =
  Pair(assertion, msg)


inline fun EvenPositive(n: Int): Int {
  require(
    constraint(n > 0, "expected $n to be > 0"),
    constraint(n > 1, "expected $n to be > 1")
  )
  return Positive(n)
}