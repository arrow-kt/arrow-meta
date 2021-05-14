package arrow.refinement

import arrow.refinement.booleans.And
import arrow.refinement.booleans.Not
import arrow.refinement.booleans.Or

/**
 * Constrains are represented by a list of pairs of [Boolean] and [String]
 * where the boolean value represents if the predicate succeeded and the
 * string value represent the error message if it failed.
 *
 * ```kotlin:ank
 * import arrow.refinement.ensure
 *
 * ensure(true to "must be true")
 * ```
 */
typealias Constraints = List<Pair<Boolean, String>>

/**
 * Builds [Constraints] from a variadic array of predicate checks and failure messages.
 * The arrow-refined compiler plugin uses this function to analyze and validate compile time calls to
 * refined types constructors
 *
 * ```kotlin:ank
 * import arrow.refinement.ensure
 *
 * ensure(true to "passed", false to "failed")
 * ```
 */
inline fun ensure(vararg constraints: Pair<Boolean, String>) : Constraints =
  constraints.toList()

/**
 * This is a compile time only function for compatibility with varargs and to cope with the
 * equality mismatch between Pair[] and Object[]
 * @suppress
 */
inline fun ensureA(vararg constraints: Any) : Constraints =
  constraints.filterIsInstance<Pair<Boolean, String>>() +
    constraints.filterIsInstance<Constraints>().flatten()

/**
 * Returns true if all the [Constraints] pairs boolean element is true
 *
 * ```kotlin:ank
 * import arrow.refinement.allValid
 * import arrow.refinement.ensure
 *
 * val constraints = ensure(true to "passed", false to "failed")
 * constraints.allValid()
 * ```
 */
inline fun Constraints.allValid(): Boolean =
  all { it.first }

/**
 * Renders in a comma separated String all messages associated to failures
 * validating [results]
 *
 * ```kotlin:ank
 * import arrow.refinement.renderMessages
 * import arrow.refinement.ensure
 *
 * val constraints = ensure(true to "passed", false to "failed")
 * renderMessages(constraints)
 * ```
 */
inline fun renderMessages(results: Constraints): String =
  results.filter { !it.first }.joinToString { it.second }

/**
 * ```kotlin:ank
 * import arrow.refinement.ensure
 * import arrow.refinement.require
 *
 * val constraints = ensure(true to "passed", false to "failed")
 * try { require(constraints) } catch (e: IllegalArgumentException) { e.message }
 * ```
 * @throws IllegalArgumentException in the event [constraints] has any invalid cases
 */
fun require(constraints: Constraints): Unit {
  if (constraints.allValid()) Unit
  else throw IllegalArgumentException(renderMessages(constraints))
}

/**
 * Validates [value] obtaining the resolved [Constraints] for the [refined]
 * predicate
 * ```kotlin:ank
 * import arrow.refinement.numbers.PositiveInt
 * import arrow.refinement.constraints
 *
 * constraints(PositiveInt, -1)
 * ```
 */
inline fun <A> constraints(refined: Refined<A, *>, value: A): Constraints =
  refined.constraints(value)

/**
 * Abstract class for all Refined predicates.
 *
 * The example below shows a refined type `Positive` that ensures [Int] is > than 0.
 * By convention we make the type companion extend the Refined class to distinguish between the
 * value type representing the types being refined and the actual predicates and their composition.
 *
 * ```kotlin:ank
 * import arrow.refinement.Refined
 * import arrow.refinement.ensure
 *
 * @JvmInline
 * value class Positive /* private constructor */ (val value: Int) {
 *  companion object : Refined<Int, Positive>(::Positive, {
 *    ensure((it > 0) to "$it should be > 0")
 *  })
 * }
 * ```
 */
abstract class Refined<A, B>(
  /**
   * Refined type constructor that will be applied if [constraints] invocation
   * results in a list of valid [Constraints]
   */
  inline val f: (A) -> B,

  /**
   * Validation function that validates [Constraints] for a given value of [A]
   */
  inline val constraints: (A) -> Constraints
) {

  /**
   * Constructor that automatically composes with [and] all [predicates]
   * as constrains to call [f]
   *
   * ```kotlin:ank
   * import arrow.refinement.numbers.PositiveInt
   * import arrow.refinement.numbers.Even
   * import arrow.refinement.Refined
   *
   * @JvmInline
   * value class PositiveEven /* private constructor */ (val value: Int) {
   *  companion object : Refined<Int, PositiveEven>(::PositiveEven, PositiveInt and Even)
   * }
   * ```
   */
  constructor(f: (A) -> B, vararg predicates: Refined<A, *>) : this(f, { a: A ->
    when {
      predicates.isEmpty() -> emptyList()
      predicates.size == 1 -> predicates[0].constraints(a)
      else -> predicates.reduce { l, r -> l + r }.constraints(a)
    }
  })

  /**
   * When using the compiler plugin calls to this function get validated at compile time when
   * using constants or suggested to use the [orNull] variant for safe access.
   *
   * If the compiler plugin is not present this function behaves the same as [require]
   *
   * @throws IllegalArgumentException if [value] is not valid
   * @return [B] if [value] is valid
   *
   * ```kotlin:ank
   * import arrow.refinement.numbers.PositiveInt
   *
   * PositiveInt(2)
   * ```
   *
   * ```kotlin:ank
   * try { PositiveInt(-1) } catch (e: IllegalArgumentException) { e.message }
   * ```
   */
  inline operator fun invoke(value: A): B {
    val results = constraints(value)
    return if (results.allValid()) f(value)
    else throw IllegalArgumentException(renderMessages(results))
  }

  /**
   * Unsafe construction.
   *
   * @throws IllegalArgumentException if [value] is not valid
   * @return [B] if [value] is valid
   *
   * ```kotlin:ank
   * PositiveInt.require(2)
   * ```
   *
   * ```kotlin:ank
   * try { PositiveInt.require(-1) } catch (e: IllegalArgumentException) { e.message }
   * ```
   */
  inline fun require(value: A): B =
    invoke(value)

  /**
   * Negates this predicate
   * @see [Not]
   */
  inline operator fun not(): Refined<A, B> =
    Not(this)

  /**
   * Safe null construction.
   * @return [B] if [value] is valid null otherwise
   *
   * ```kotlin:ank
   * PositiveInt.orNull(2)
   * ```
   *
   * ```kotlin:ank
   * PositiveInt.orNull(-1)
   * ```
   */
  inline fun orNull(value: A): B? =
    if (constraints(value).allValid()) f(value)
    else null

  /**
   * @return true if [value] is valid false otherwise
   *
   * ```kotlin:ank
   * PositiveInt.isValid(2)
   * ```
   *
   * ```kotlin:ank
   * PositiveInt.isValid(-1)
   * ```
   */
  inline fun isValid(value: A): Boolean =
    constraints(value).allValid()

  /**
   * Validates [value] invoking [ifInvalid] in the case of failure and [ifValid] in the case of success.
   *
   * ```kotlin:ank
   * import arrow.refinement.numbers.PositiveInt
   *
   * PositiveInt.fold(2, { "failed: $it" }, { "success: $it" })
   * ```
   *
   * ```kotlin:ank
   * import arrow.refinement.numbers.PositiveInt
   * PositiveInt.fold(-1, { "failed: $it" }, { "success: $it" })
   * ```
   */
  inline fun <C> fold(value: A, ifInvalid: (Constraints) -> C, ifValid: (B) -> C): C {
    val results = constraints(value)
    return if (results.allValid()) ifValid(f(value))
    else {
      val failedConstrains = results.filterNot { it.first }
      ifInvalid(failedConstrains)
    }
  }

  /**
   * Composition of all the [Constraints] of this predicate with the constraints of [other]
   * @see [And]
   */
  inline operator fun <C> plus(other: Refined<A, C>): Refined<A, C> =
    And(this, other)

  /**
   * Composition of either the [Constraints] of this predicate or the constraints of [other]
   * @see [Or]
   */
  inline infix fun <C> or(other: Refined<A, C>): Refined<A, C> =
    Or(this, other)

  /**
   * Composition of all the [Constraints] of this predicate with the constraints of [other]
   * @see [And]
   */
  inline infix fun <C> and(other: Refined<A, C>): Refined<A, C> =
    And(this, other)

}
