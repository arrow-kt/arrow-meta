package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class ValidInt private constructor(val value: String) {
  companion object : Refined<String, ValidInt>(::ValidInt, {
    ensure((it.toIntOrNull() != null) to ("Expected $it to be a valid Int"))
  })
}