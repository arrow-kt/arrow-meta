package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import arrow.refinement.strings.EndsWith.Value
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [IPv4] constrains [String] to be a valid IPv4
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.IPv4
 *
 * IPv4.orNull("192.168.1.1")
 * ```
 *
 * ```kotlin:ank
 * IPv4.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * IPv4.constraints("192.168.1.1")
 * ```
 *
 * ```kotlin:ank
 * IPv4.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * IPv4.isValid("192.168.1.1")
 * ```
 *
 * ```kotlin:ank
 * IPv4.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * IPv4.fold("192.168.1.1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * IPv4.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * IPv4.require("192.168.1.1")
 * ```
 *
 * ```kotlin:ank
 * try { IPv4.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class IPv4 private constructor(val value: String) {
  companion object : Refined<String, IPv4>(::IPv4, {
    ensure((it.matches(Companion.IPV4Regex)) to ("Expected $it to be an IPv4 address"))
  }) {
    /**
     * Credit for regex goes to https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
     */
    val IPV4Regex = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$".toRegex()
  }
}