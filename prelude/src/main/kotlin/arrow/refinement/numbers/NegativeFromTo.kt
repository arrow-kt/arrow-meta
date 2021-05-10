package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.and

@JvmInline
value class NegativeFromTo private constructor(val value: Int) {
  class N(from: UInt, to: UInt, msg: (Int) -> String = { "$it should be in range $from..$to" }) :
    Refined<Int, NegativeFromTo>(::NegativeFromTo, From.N(from) and To.N(to))
}