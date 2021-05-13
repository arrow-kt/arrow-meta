package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith

@JvmInline
value
/**
 * [Rfc3927LocalLinkNetwork] constrains [String] to be an [IPv4] that [StartsWith] `169.254.`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc3927LocalLinkNetwork
 *
 * Rfc3927LocalLinkNetwork.orNull("169.254.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.constraints("169.254.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc3927LocalLinkNetwork.isValid("169.254.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.fold("169.254.0.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc3927LocalLinkNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc3927LocalLinkNetwork.require("169.254.0.1")
 * ```
 *
 */
class Rfc3927LocalLinkNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc3927LocalLinkNetwork>(
    ::Rfc3927LocalLinkNetwork, IPv4 and StartsWith.Value("169.254.")
  )
}