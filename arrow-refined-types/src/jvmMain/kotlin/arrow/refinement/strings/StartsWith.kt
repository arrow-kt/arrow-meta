package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class StartsWith private constructor(val value: String) {
  class Value(value: String, msg: (String) -> String? = { null }) : Refined<String, StartsWith>(::StartsWith, {
    ensure((it.startsWith(value, ignoreCase = false)) to (msg(it) ?: "Expected $it to start with $value"))
  })
}