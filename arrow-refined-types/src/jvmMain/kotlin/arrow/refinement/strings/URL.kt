package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import java.net.MalformedURLException

@JvmInline
value class URL private constructor(val value: String) {
  companion object : Refined<String, URL>(::URL, {
    ensure(
      (try {
        java.net.URL(it)
        true
      } catch (e: MalformedURLException) {
        false
      }) to ("Expected $it to be a valid URL")
    )
  })
}