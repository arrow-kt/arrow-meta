package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Rfc1918ClassCPrivateNetwork] constrains [String] to an [IPv4] that [StartsWith] `192.168.`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc1918ClassCPrivateNetwork
 *
 * Rfc1918ClassCPrivateNetwork.orNull("192.168.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.constraints("192.168.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.isValid("192.168.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.fold("192.168.0.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc1918ClassCPrivateNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassCPrivateNetwork.require("192.168.0.1")
 * ```
 *
 */
class Rfc1918ClassCPrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc1918ClassCPrivateNetwork>(
    ::Rfc1918ClassCPrivateNetwork, IPv4 and StartsWith.Value("192.168.")
  )
}
