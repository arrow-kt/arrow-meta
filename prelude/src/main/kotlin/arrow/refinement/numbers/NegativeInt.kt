package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

@JvmInline
value class NegativeInt private constructor(val value: Int) {
  companion object : Refined<Int, NegativeInt>(::NegativeInt, Not(PositiveInt) {
    "$it should be < 0"
  })
}