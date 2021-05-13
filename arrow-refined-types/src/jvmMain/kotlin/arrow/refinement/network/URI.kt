package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [URI] constrains [String] to be a valid hexadecimal [String] of length 32
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.URI
 *
 * URI.orNull("urn:isbn:0-476-27557-4")
 * ```
 *
 * ```kotlin:ank
 * URI.orNull("not-uri")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * URI.constraints("urn:isbn:0-476-27557-4")
 * ```
 *
 * ```kotlin:ank
 * URI.constraints("not-uri")
 * ```
 *
 *  ```kotlin:ank
 * URI.isValid("urn:isbn:0-476-27557-4")
 * ```
 *
 * ```kotlin:ank
 * URI.isValid("not-uri")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * URI.fold("urn:isbn:0-476-27557-4", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * URI.fold("not-uri", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * URI.require("urn:isbn:0-476-27557-4")
 * ```
 *
 * ```kotlin:ank
 * try { URI.require("not-uri") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class URI private constructor(val value: String) {
  companion object : Refined<String, URI>(::URI, {
    ensure(
      (try {
        java.net.URI.create(it)
        true
      } catch (e: IllegalArgumentException) {
        false
      }) to ("Expected $it to be a valid URI")
    )
  })
}