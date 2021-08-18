package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [PortNumber] constrains [Int] to be in range of 0..65535
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.PortNumber
 *
 * PortNumber.orNull(55555)
 * ```
 *
 * ```kotlin:ank
 * PortNumber.orNull(100000)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * PortNumber.constraints(55555)
 * ```
 *
 * ```kotlin:ank
 * PortNumber.constraints(100000)
 * ```
 *
 *  ```kotlin:ank
 * PortNumber.isValid(55555)
 * ```
 *
 * ```kotlin:ank
 * PortNumber.isValid(100000)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * PortNumber.fold(55555, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PortNumber.fold(100000, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { PortNumber.require(100000) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * PortNumber.require(55555)
 * ```
 *
 */
class PortNumber private constructor(val value: Int) {
  companion object : Refined<Int, PortNumber>(::PortNumber, {
    ensure((it in 0..65535) to "$it should be in the closed range of 0..65535 to be a valid port number")
  })
}