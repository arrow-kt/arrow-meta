package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class NonSystemPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, NonSystemPortNumber>(::NonSystemPortNumber, {
    ensure((it in 1024..65535) to "$it should be in the closed range of 1024..65535 to be a valid dynamic port number")
  })
}