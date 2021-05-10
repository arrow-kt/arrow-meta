package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class ValidDouble private constructor(val value: String) {
  companion object : Refined<String, ValidDouble>(::ValidDouble, {
    ensure((it.toDoubleOrNull() != null) to ("Expected $it to be a valid Double"))
  })
}