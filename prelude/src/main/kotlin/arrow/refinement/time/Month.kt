package arrow.refinement.time

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Month private constructor(val value: Int) {
  companion object : Refined<Int, Month>(::Month, {
    ensure((it in 1..12) to "$it should be in the closed range of 1..12 to be a valid month number")
  })
}