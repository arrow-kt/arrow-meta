package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Hour] constrains an [Int] to be in range of 0..23
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.time.Hour
 *
 * Hour.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Hour.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Hour.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Hour.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Hour.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Hour.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Hour.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Hour.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Hour.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Hour.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Hour private constructor(val value: Int) {
  companion object : Refined<Int, Hour>(::Hour, {
    ensure((it in 0..23) to "$it should be in the closed range of 0..23 to be a valid hour number")
  })
}
