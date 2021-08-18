package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

/**
 * [NotZero] constrains an [Int] to be != 0
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.NotZero
 *
 * NotZero.orNull(0)
 * ```
 *
 * ```kotlin:ank
 * NotZero.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * NotZero.constraints(0)
 * ```
 *
 * ```kotlin:ank
 * NotZero.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * NotZero.isValid(0)
 * ```
 *
 * ```kotlin:ank
 * NotZero.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * NotZero.fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * NotZero.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * NotZero.require(-1)
 * ```
 *
 * ```kotlin:ank
 * try { NotZero.require(0) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
*/
object NotZero : Refined<Int, NotZero>({ NotZero }, Not(Zero) {
  "should not be 0"
})