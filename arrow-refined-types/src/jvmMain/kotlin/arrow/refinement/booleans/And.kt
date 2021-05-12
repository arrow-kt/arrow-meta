package arrow.refinement.booleans

import arrow.refinement.Refined

/**
 * Boolean conjunction of the [left] and [right] predicates.
 * left and right
 *
 * In the example below we can see how we can use `+` to compose the predicates
 * of [arrow.refinement.numbers.PositiveInt] and [arrow.refinement.numbers.Even]
 *
 * ```kotlin:ank
 * import arrow.refinement.Refined
 * import arrow.refinement.numbers.PositiveInt
 * import arrow.refinement.numbers.Even
 *
 * @JvmInline
 * value class PositiveEven /* private constructor */ (val value: Int) {
 *   companion object:
 *     Refined<Int, PositiveEven>(::PositiveEven, PositiveInt + Even)
 * }
 * ```
 *
 *  # Safe nullable construction
 *
 * ```kotlin:ank
 * PositiveEven.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveEven.orNull(1)
 * ```
 *
 * ```kotlin:ank
 * PositiveEven.orNull(-2)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * PositiveEven.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveEven.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * PositiveEven.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveEven.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * PositiveEven.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PositiveEven.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PositiveEven.fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * PositiveEven.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { PositiveEven.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * @see arrow.refinement.Refined.plus
 * @see arrow.refinement.and
 */
class And<A, B>(
  private val left: Refined<A, *>,
  private val right: Refined<A, B>,
  defaultMsg: String? = null
) :
  Refined<A, B>({ right(it) }, {
    val newConstrains = left.constraints(it).toList() + right.constraints(it).toList()
    val compositionResults = newConstrains.map { (k, v) -> k to (defaultMsg ?: v) }
    compositionResults
  })