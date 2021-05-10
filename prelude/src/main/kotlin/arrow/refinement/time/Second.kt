package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Second private constructor(val value: Int) {
  companion object : Refined<Int, Second>(::Second, {
    ensure((it in 0..59) to "$it should be in the closed range of 0..59 to be a valid second of minute number")
  })
}