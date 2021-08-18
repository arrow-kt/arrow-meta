package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Rfc5737Testnet3Network] constrains [String] to be an [IPv4] that [StartsWith] `203.0.113..`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc5737Testnet3Network
 *
 * Rfc5737Testnet3Network.orNull("203.0.113.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.constraints("203.0.113.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc5737Testnet3Network.isValid("203.0.113.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.fold("203.0.113.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc5737Testnet3Network.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc5737Testnet3Network.require("203.0.113.1")
 * ```
 *
 */
class Rfc5737Testnet3Network private constructor(val value: String) {
  companion object : Refined<String, Rfc5737Testnet3Network>(
    ::Rfc5737Testnet3Network, IPv4 and StartsWith.Value("203.0.113.")
  )
}
