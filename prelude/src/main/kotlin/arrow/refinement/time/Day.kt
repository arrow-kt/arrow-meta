package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Day private constructor(val value: Int) {
  companion object : Refined<Int, Day>(::Day, {
    ensure((it in 1..31) to "$it should be in the closed range of 1..31 to be a valid day number")
  })
}