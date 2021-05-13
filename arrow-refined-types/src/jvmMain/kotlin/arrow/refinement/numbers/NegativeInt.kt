package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

@JvmInline
value
/**
 * [NegativeInt] constrains an [Int] to be < 0
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.NegativeInt
 *
 * NegativeInt.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * NegativeInt.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * NegativeInt.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * NegativeInt.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * NegativeInt.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * NegativeInt.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * NegativeInt.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * NegativeInt.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * NegativeInt.require(-1)
 * ```
 *
 * ```kotlin:ank
 * try { NegativeInt.require(2) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class NegativeInt private constructor(val value: Int) {
  companion object : Refined<Int, NegativeInt>(::NegativeInt, Not(PositiveInt) {
    "$it should be < 0"
  })
}