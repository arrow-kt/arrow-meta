package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class SystemPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, SystemPortNumber>(::SystemPortNumber, {
    ensure((it in 0..1023) to "$it should be in the closed range of 0..1023 to be a valid system port number")
  })
}