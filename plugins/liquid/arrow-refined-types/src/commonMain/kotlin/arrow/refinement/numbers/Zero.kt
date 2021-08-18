package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

/**
 * [Zero] constrains an [Int] to be == 0
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.Zero
 *
 * Zero.orNull(0)
 * ```
 *
 * ```kotlin:ank
 * Zero.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Zero.constraints(0)
 * ```
 *
 * ```kotlin:ank
 * Zero.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Zero.isValid(0)
 * ```
 *
 * ```kotlin:ank
 * Zero.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Zero.fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Zero.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Zero.require(0)
 * ```
 *
 * ```kotlin:ank
 * try { Zero.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
object Zero : Refined<Int, Zero>({ Zero }, {
  ensure((it == 0) to "Expected $it to be 0")
}) {
  const val value = 0
}
