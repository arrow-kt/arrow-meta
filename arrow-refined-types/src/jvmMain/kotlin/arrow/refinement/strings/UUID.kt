package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class UUID private constructor(val value: String) {
  companion object : Refined<String, UUID>(::UUID, {
    ensure(
      (try {
        java.util.UUID.fromString(it)
        true
      } catch (e: IllegalArgumentException) {
        false
      }) to ("Expected $it to be a valid URL")
    )
  })
}