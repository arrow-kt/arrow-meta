package arrow.refinement.network

import arrow.refinement.Refined

@JvmInline
value
/**
 * [Rfc5737TestnetNetwork] constrains [String] to be either of
 * [Rfc5737Testnet1Network] or [Rfc5737Testnet2Network] or [Rfc5737Testnet3Network]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc5737TestnetNetwork
 *
 * Rfc5737TestnetNetwork.orNull("192.0.2.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.constraints("192.0.2.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc5737TestnetNetwork.isValid("192.0.2.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.fold("192.0.2.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc5737TestnetNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc5737TestnetNetwork.require("192.0.2.1")
 * ```
 *
 */
class Rfc5737TestnetNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc5737TestnetNetwork>(
    ::Rfc5737TestnetNetwork, Rfc5737Testnet1Network or Rfc5737Testnet2Network or Rfc5737Testnet3Network
  )
}