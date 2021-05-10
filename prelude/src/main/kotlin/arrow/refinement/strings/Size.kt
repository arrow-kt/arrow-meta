package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Size private constructor(val value: String) {
  class N(value: UInt, msg: (String) -> String? = { null }) : Refined<String, Size>(::Size, {
    ensure((it.length == value.toInt()) to (msg(it) ?: "Expected $it to have length $value"))
  })
}