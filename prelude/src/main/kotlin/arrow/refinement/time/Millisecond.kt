package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Millisecond private constructor(val value: Int) {
  companion object : Refined<Int, Millisecond>(::Millisecond, {
    ensure((it in 0..999) to "$it should be in the closed range of 0..999 to be a valid millisecond of second number")
  })
}
