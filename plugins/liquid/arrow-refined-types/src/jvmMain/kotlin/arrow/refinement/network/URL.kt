package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure
import java.net.MalformedURLException

@JvmInline
value
/**
 * JVM only
 *
 * [URL] constrains [String] to be validated by [java.net.URL]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.URL
 *
 * URL.orNull("https://arrow-kt.io/")
 * ```
 *
 * ```kotlin:ank
 * URL.orNull("not-url")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * URL.constraints("https://arrow-kt.io/")
 * ```
 *
 * ```kotlin:ank
 * URL.constraints("not-url")
 * ```
 *
 *  ```kotlin:ank
 * URL.isValid("https://arrow-kt.io/")
 * ```
 *
 * ```kotlin:ank
 * URL.isValid("not-url")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * URL.fold("https://arrow-kt.io/", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * URL.fold("not-url", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * URL.require("https://arrow-kt.io/")
 * ```
 *
 * ```kotlin:ank
 * try { URL.require("not-url") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class URL private constructor(val value: String) {
  companion object : Refined<String, URL>(::URL, {
    ensure(
      (try {
        java.net.URL(it)
        true
      } catch (e: MalformedURLException) {
        false
      }) to ("Expected $it to be a valid URL")
    )
  })
}
