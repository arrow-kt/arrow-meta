package arrow.refinement.booleans

import arrow.refinement.Refined
import arrow.refinement.allValid

/**
 * Boolean disjunction of the [left] and [right] predicates.
 * left or right
 *
 * In the example below we can see how we can use `or` to compose the predicates
 * of [arrow.refinement.numbers.PositiveInt] and [arrow.refinement.numbers.Even]
 *
 * ```kotlin:ank
 * import arrow.refinement.Refined
 * import arrow.refinement.numbers.PositiveInt
 * import arrow.refinement.numbers.Even
 *
 * @JvmInline
 * value class PositiveOrEven /* private constructor */ (val value: Int) {
 *   companion object:
 *     Refined<Int, PositiveOrEven>(::PositiveOrEven, PositiveInt or Even)
 * }
 * ```
 *
 *  # Safe nullable construction
 *
 * ```kotlin:ank
 * PositiveOrEven.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveOrEven.orNull(1)
 * ```
 *
 * ```kotlin:ank
 * PositiveOrEven.orNull(-2)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * PositiveOrEven.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveOrEven.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * PositiveOrEven.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveOrEven.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * PositiveOrEven.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PositiveOrEven.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PositiveOrEven.fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * PositiveOrEven.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { PositiveOrEven.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * @see arrow.refinement.Refined.or
 *
 */
class Or<A, B>(
  private val left: Refined<A, *>,
  private val right: Refined<A, B>,
  defaultMsg: String? = null
) :
  Refined<A, B>({ right(it) }, { a ->
    val leftResults = left.constraints(a)
    val rightResults = right.constraints(a)
    val leftValidResults = leftResults.filter { it.first }
    val rightValidResults = rightResults.filter { it.first }
    val newConstrains =
      when {
        leftResults.allValid() -> leftResults + rightValidResults
        rightResults.allValid() -> rightResults + leftValidResults
        else -> leftResults + rightResults
      }
    newConstrains.map { (k, v) -> k to (defaultMsg ?: v) }.distinct()
  })
