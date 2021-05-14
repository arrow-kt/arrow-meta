package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [NonSystemPortNumber] constrains [Int] to be in range of 1024..65535
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.NonSystemPortNumber
 *
 * NonSystemPortNumber.orNull(55555)
 * ```
 *
 * ```kotlin:ank
 * NonSystemPortNumber.orNull(22)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * NonSystemPortNumber.constraints(55555)
 * ```
 *
 * ```kotlin:ank
 * NonSystemPortNumber.constraints(22)
 * ```
 *
 *  ```kotlin:ank
 * NonSystemPortNumber.isValid(55555)
 * ```
 *
 * ```kotlin:ank
 * NonSystemPortNumber.isValid(22)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * NonSystemPortNumber.fold(55555, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * NonSystemPortNumber.fold(22, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { NonSystemPortNumber.require(22) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * NonSystemPortNumber.require(55555)
 * ```
 *
 */
class NonSystemPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, NonSystemPortNumber>(::NonSystemPortNumber, {
    ensure((it in 1024..65535) to "$it should be in the closed range of 1024..65535 to be a valid system port number")
  })
}