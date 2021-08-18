package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Month] constrains an [Int] to be in range of 1..12
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.time.Month
 *
 * Month.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Month.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Month.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Month.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Month.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Month.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Month.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Month.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Month.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Month.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Month private constructor(val value: Int) {
  companion object : Refined<Int, Month>(::Month, {
    ensure((it in 1..12) to "$it should be in the closed range of 1..12 to be a valid month number")
  })
}
