package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Hour private constructor(val value: Int) {
  companion object : Refined<Int, Hour>(::Hour, {
    ensure((it in 0..23) to "$it should be in the closed range of 0..23 to be a valid hour number")
  })
}