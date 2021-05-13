package arrow.refinement.network

import arrow.refinement.Refined

@JvmInline
value
/**
 * [Rfc1918PrivateNetwork] constrains [String] to be either of
 * [Rfc1918ClassAPrivateNetwork], [Rfc1918ClassBPrivateNetwork] or [Rfc1918PrivateNetwork]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc1918PrivateNetwork
 *
 * Rfc1918PrivateNetwork.orNull("10.0.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.constraints("10.0.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc1918PrivateNetwork.isValid("10.0.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.fold("10.0.0.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc1918PrivateNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc1918PrivateNetwork.require("10.0.0.1")
 * ```
 *
 */
class Rfc1918PrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc1918PrivateNetwork>(
    ::Rfc1918PrivateNetwork, Rfc1918ClassAPrivateNetwork or Rfc1918ClassBPrivateNetwork or Rfc1918ClassCPrivateNetwork
  )
}