package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value class
/**
 * [IPv6] constrains [String] to be a valid IPv6
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.IPv6
 *
 * IPv6.orNull("2001:0db8:85a3:0000:0000:8a2e:0370:7334")
 * ```
 *
 * ```kotlin:ank
 * IPv6.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * IPv6.constraints("2001:0db8:85a3:0000:0000:8a2e:0370:7334")
 * ```
 *
 * ```kotlin:ank
 * IPv6.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * IPv6.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334")
 * ```
 *
 * ```kotlin:ank
 * IPv6.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * IPv6.fold("2001:0db8:85a3:0000:0000:8a2e:0370:7334", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * IPv6.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * IPv6.require("2001:0db8:85a3:0000:0000:8a2e:0370:7334")
 * ```
 *
 * ```kotlin:ank
 * try { IPv6.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
IPv6 private constructor(val value: String) {
  companion object : Refined<String, IPv6>(::IPv6, {
    ensure((it.matches(Companion.IPV6Regex)) to ("Expected $it to be an IPv6 address"))
  }) {
    /**
     * Credit for regex goes to https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses
     */
    val IPV6Regex =
      "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))".toRegex()
  }
}
