package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Minute] constrains an [Int] to be in range of 0..59
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.time.Minute
 *
 * Minute.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Minute.orNull(-1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Minute.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Minute.constraints(-1)
 * ```
 *
 *  ```kotlin:ank
 * Minute.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Minute.isValid(-1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Minute.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Minute.fold(-1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Minute.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Minute.require(-1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Minute private constructor(val value: Int) {
  companion object : Refined<Int, Minute>(::Minute, {
    ensure((it in 0..59) to "$it should be in the closed range of 0..59 to be a valid minute number")
  })
}