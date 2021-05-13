package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

@JvmInline
value
/**
 * [Odd] constrains an [Int] to not be [Even]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.Odd
 *
 * Odd.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Odd.orNull(1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Odd.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Odd.constraints(1)
 * ```
 *
 *  ```kotlin:ank
 * Odd.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Odd.isValid(1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Odd.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Odd.fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Odd.require(1)
 * ```
 *
 * ```kotlin:ank
 * try { Odd.require(2) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Odd private constructor(val value: Int) {
  companion object : Refined<Int, Odd>(::Odd, Not(Even) {
    "$it should be odd"
  })
}