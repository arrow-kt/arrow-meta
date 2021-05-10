package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class IPv4 private constructor(val value: String) {
  companion object : Refined<String, IPv4>(::IPv4, {
    ensure((it.matches(Companion.IPV4Regex)) to ("Expected $it to be an IPv4 address"))
  }) {
    /**
     * Credit for regex goes to https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
     */
    val IPV4Regex = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$".toRegex()
  }
}