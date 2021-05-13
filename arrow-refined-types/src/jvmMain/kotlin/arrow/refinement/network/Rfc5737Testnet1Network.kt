package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith

@JvmInline
value
/**
 * [Rfc5737Testnet1Network] constrains [String] to be an [IPv4] that [StartsWith] `192.0.2.`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc5737Testnet1Network
 *
 * Rfc5737Testnet1Network.orNull("192.0.2.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.constraints("192.0.2.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc5737Testnet1Network.isValid("192.0.2.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.fold("192.0.2.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc5737Testnet1Network.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet1Network.require("192.0.2.1")
 * ```
 *
 */
class Rfc5737Testnet1Network private constructor(val value: String) {
  companion object : Refined<String, Rfc5737Testnet1Network>(
    ::Rfc5737Testnet1Network, IPv4 and StartsWith.Value("192.0.2.")
  )
}