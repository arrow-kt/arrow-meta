package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class MatchesRegex private constructor(val value: String) {
  class Regex(value: kotlin.text.Regex, msg: (String) -> String? = { null }) : Refined<String, MatchesRegex>(::MatchesRegex, {
    ensure((it.matches(value)) to (msg(it) ?: "Expected $it to match $value"))
  })
}