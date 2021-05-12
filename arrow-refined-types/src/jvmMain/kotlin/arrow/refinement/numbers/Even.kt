package arrow.refinement.numbers

import arrow.refinement.Refined

@JvmInline
value class Even private constructor(val value: Int) {
  companion object : Refined<Int, Even>(::Even, DivisibleBy.N(2u) {
    "$it should be even"
  })
}