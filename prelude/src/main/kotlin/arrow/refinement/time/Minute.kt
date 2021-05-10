package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Minute private constructor(val value: Int) {
  companion object : Refined<Int, Minute>(::Minute, {
    ensure((it in 0..59) to "$it should be in the closed range of 0..59 to be a valid minute number")
  })
}