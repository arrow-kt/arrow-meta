package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [DynamicPortNumber] constrains [Int] to be in range of 49152..65535
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.DynamicPortNumber
 *
 * DynamicPortNumber.orNull(55555)
 * ```
 *
 * ```kotlin:ank
 * DynamicPortNumber.orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * DynamicPortNumber.constraints(55555)
 * ```
 *
 * ```kotlin:ank
 * DynamicPortNumber.constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * DynamicPortNumber.isValid(55555)
 * ```
 *
 * ```kotlin:ank
 * DynamicPortNumber.isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * DynamicPortNumber.fold(55555, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * DynamicPortNumber.fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { DynamicPortNumber.require(0) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * DynamicPortNumber.require(55555)
 * ```
 *
 */
class DynamicPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, DynamicPortNumber>(::DynamicPortNumber, {
    ensure((it in 49152..65535) to "$it should be in the closed range of 49152..65535 to be a valid dynamic port number")
  })
}