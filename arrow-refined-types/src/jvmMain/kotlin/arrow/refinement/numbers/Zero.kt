package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

object Zero : Refined<Int, Zero>({ Zero }, {
  ensure((it == 0) to "$it should be 0")
}) {
  const val value = 0
}