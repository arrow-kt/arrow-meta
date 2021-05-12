package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class PortNumber private constructor(val value: Int) {
  companion object : Refined<Int, PortNumber>(::PortNumber, {
    ensure((it in 0..65535) to "$it should be in the closed range of 0..65535 to be a valid port number")
  })
}