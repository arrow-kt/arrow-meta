package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class PositiveInt private constructor(val value: Int) {
  companion object : Refined<Int, PositiveInt>(::PositiveInt, {
    ensure((it > 0) to "$it should be > 0")
  })
}