package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Day] constrains an [Int] to be in range of 1..31
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.time.Day
 *
 * Day.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Day.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Day.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Day.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Day.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Day.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Day.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Day.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Day.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Day.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Day private constructor(val value: Int) {
  companion object : Refined<Int, Day>(::Day, {
    ensure((it in 1..31) to "$it should be in the closed range of 1..31 to be a valid day number")
  })
}