package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Rfc1918ClassAPrivateNetwork] constrains [String] to an [IPv4] that [StartsWith] `10.`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc1918ClassAPrivateNetwork
 *
 * Rfc1918ClassAPrivateNetwork.orNull("10.0.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.constraints("10.0.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.isValid("10.0.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.fold("10.0.0.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc1918ClassAPrivateNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassAPrivateNetwork.require("10.0.0.1")
 * ```
 *
 */
class Rfc1918ClassAPrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc1918ClassAPrivateNetwork>(
    ::Rfc1918ClassAPrivateNetwork, IPv4 and StartsWith.Value("10.")
  )
}