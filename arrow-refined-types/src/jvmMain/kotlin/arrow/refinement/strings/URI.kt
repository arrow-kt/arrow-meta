package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class URI private constructor(val value: String) {
  companion object : Refined<String, URI>(::URI, {
    ensure(
      (try {
        java.net.URI.create(it)
        true
      } catch (e: IllegalArgumentException) {
        false
      }) to ("Expected $it to be a valid URI")
    )
  })
}