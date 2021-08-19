package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Rfc5737Testnet2Network] constrains [String] to be an [IPv4] that [StartsWith] `198.51.100.`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc5737Testnet2Network
 *
 * Rfc5737Testnet2Network.orNull("198.51.100.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.constraints("198.51.100.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc5737Testnet2Network.isValid("198.51.100.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.fold("198.51.100.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc5737Testnet2Network.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet2Network.require("198.51.100.1")
 * ```
 *
 */
class Rfc5737Testnet2Network private constructor(val value: String) {
  companion object : Refined<String, Rfc5737Testnet2Network>(
    ::Rfc5737Testnet2Network, IPv4 and StartsWith.Value("198.51.100.")
  )
}
