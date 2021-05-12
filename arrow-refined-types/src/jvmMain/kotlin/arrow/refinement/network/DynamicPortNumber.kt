package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class DynamicPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, DynamicPortNumber>(::DynamicPortNumber, {
    ensure((it in 49152..65535) to "$it should be in the closed range of 49152..65535 to be a valid dynamic port number")
  })
}