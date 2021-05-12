package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

@JvmInline
value class Odd private constructor(val value: Int) {
  companion object : Refined<Int, Odd>(::Odd, Not(Even) {
    "$it should be odd"
  })
}