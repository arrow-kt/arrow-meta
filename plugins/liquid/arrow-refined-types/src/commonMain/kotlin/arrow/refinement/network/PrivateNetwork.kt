package arrow.refinement.network

import arrow.refinement.Refined
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [PrivateNetwork] constrains [String] to match any of the types
 * [Rfc1918PrivateNetwork], [Rfc5737TestnetNetwork], [Rfc3927LocalLinkNetwork] and [Rfc2544BenchmarkNetwork]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.PrivateNetwork
 *
 * PrivateNetwork.orNull("192.168.1.2")
 * ```
 *
 * ```kotlin:ank
 * PrivateNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * PrivateNetwork.constraints("192.168.1.2")
 * ```
 *
 * ```kotlin:ank
 * PrivateNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * PrivateNetwork.isValid("192.168.1.2")
 * ```
 *
 * ```kotlin:ank
 * PrivateNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * PrivateNetwork.fold("192.168.1.2", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * PrivateNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { PrivateNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * PrivateNetwork.require("192.168.1.2")
 * ```
 *
 */
class PrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, PrivateNetwork>(
    ::PrivateNetwork, Rfc1918PrivateNetwork or Rfc5737TestnetNetwork or Rfc3927LocalLinkNetwork or Rfc2544BenchmarkNetwork
  )
}
