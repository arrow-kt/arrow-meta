package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.MatchesRegex
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Rfc1918ClassBPrivateNetwork] constrains [String] to an [IPv4] that [MatchesRegex] `^172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\..+`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc1918ClassBPrivateNetwork
 *
 * Rfc1918ClassBPrivateNetwork.orNull("172.17.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.constraints("172.17.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.isValid("172.17.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.fold("172.17.0.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc1918ClassBPrivateNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc1918ClassBPrivateNetwork.require("172.17.0.1")
 * ```
 *
 */
class Rfc1918ClassBPrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc1918ClassBPrivateNetwork>(
    ::Rfc1918ClassBPrivateNetwork,
    IPv4 and MatchesRegex.Regex("^172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\..+".toRegex())
  )
}
