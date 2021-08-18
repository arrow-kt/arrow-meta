package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Millisecond] constrains an [Int] to be in range of 0..999
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.time.Millisecond
 *
 * Millisecond.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Millisecond.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Millisecond.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Millisecond.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Millisecond.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Millisecond.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Millisecond.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Millisecond.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Millisecond.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Millisecond.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Millisecond private constructor(val value: Int) {
  companion object : Refined<Int, Millisecond>(::Millisecond, {
    ensure((it in 0..999) to "$it should be in the closed range of 0..999 to be a valid millisecond of second number")
  })
}
