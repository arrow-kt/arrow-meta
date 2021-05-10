package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class HexString private constructor(val value: String) {
  companion object : Refined<String, HexString>(::HexString, {
    ensure((it.matches("^(([0-9a-f]+)|([0-9A-F]+))$".toRegex())) to ("Expected $it to be a valid hexadecimal string"))
  })
}
