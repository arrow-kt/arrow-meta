package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class UserPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, UserPortNumber>(::UserPortNumber, {
    ensure((it in 1024..49151) to "$it should be in the closed range of 1024..49151 to be a valid user port number")
  })
}