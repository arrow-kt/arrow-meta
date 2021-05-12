package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class EndsWith private constructor(val value: String) {
  class Value(value: String, msg: (String) -> String? = { null }) : Refined<String, EndsWith>(::EndsWith, {
    ensure((it.endsWith(value, ignoreCase = false)) to (msg(it) ?: "Expected $it to end with $value"))
  })
}