package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class ValidLong private constructor(val value: String) {
  companion object : Refined<String, ValidLong>(::ValidLong, {
    ensure((it.toLongOrNull() != null) to ("Expected $it to be a valid Long"))
  })
}