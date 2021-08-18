package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Rfc2544BenchmarkNetwork] constrains [String] to be an [IPv4] that [StartsWith] `198.18.` or `198.19.`
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.Rfc2544BenchmarkNetwork
 *
 * Rfc2544BenchmarkNetwork.orNull("198.18.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.orNull("80.34.200.60")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.constraints("198.18.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.constraints("80.34.200.60")
 * ```
 *
 *  ```kotlin:ank
 * Rfc2544BenchmarkNetwork.isValid("198.18.0.1")
 * ```
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.isValid("80.34.200.60")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.fold("198.18.0.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.fold("80.34.200.60", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Rfc2544BenchmarkNetwork.require("80.34.200.60") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Rfc2544BenchmarkNetwork.require("198.18.0.1")
 * ```
 *
 */
class Rfc2544BenchmarkNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc2544BenchmarkNetwork>(
    ::Rfc2544BenchmarkNetwork, IPv4 and (StartsWith.Value("198.18.") or StartsWith.Value("198.19."))
  )
}