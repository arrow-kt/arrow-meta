package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [PositiveInt] constrains an [Int] to be > 0
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.PositiveInt
 *
 * PositiveInt.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveInt.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * PositiveInt.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveInt.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * PositiveInt.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * PositiveInt.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * PositiveInt.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PositiveInt.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * PositiveInt.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { PositiveInt.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class PositiveInt private constructor(val value: Int) {
  companion object : Refined<Int, PositiveInt>(::PositiveInt, {
    ensure((it > 0) to "$it should be > 0")
  })
}