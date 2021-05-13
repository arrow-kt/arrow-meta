package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [SystemPortNumber] constrains [Int] to be in range of 0..1023
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.SystemPortNumber
 *
 * SystemPortNumber.orNull(1000)
 * ```
 *
 * ```kotlin:ank
 * SystemPortNumber.orNull(100000)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * SystemPortNumber.constraints(1000)
 * ```
 *
 * ```kotlin:ank
 * SystemPortNumber.constraints(100000)
 * ```
 *
 *  ```kotlin:ank
 * SystemPortNumber.isValid(1000)
 * ```
 *
 * ```kotlin:ank
 * SystemPortNumber.isValid(100000)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * SystemPortNumber.fold(1000, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * SystemPortNumber.fold(100000, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { SystemPortNumber.require(100000) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * SystemPortNumber.require(1000)
 * ```
 *
 */
class SystemPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, SystemPortNumber>(::SystemPortNumber, {
    ensure((it in 0..1023) to "$it should be in the closed range of 0..1023 to be a valid system port number")
  })
}