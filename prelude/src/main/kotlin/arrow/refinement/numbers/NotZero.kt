package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

object NotZero : Refined<Int, NotZero>({ NotZero }, Not(Zero) {
  "$it should not be 0"
})