package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Second] constrains an [Int] to be in range of 0..59
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.time.Second
 *
 * Second.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Second.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Second.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Second.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Second.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Second.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Second.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Second.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Second.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Second.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Second private constructor(val value: Int) {
  companion object : Refined<Int, Second>(::Second, {
    ensure((it in 0..59) to "$it should be in the closed range of 0..59 to be a valid second of minute number")
  })
}
