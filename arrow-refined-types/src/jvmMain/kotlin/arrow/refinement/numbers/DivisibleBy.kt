package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class DivisibleBy private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be divisible by $n" }) :
    Refined<Int, DivisibleBy>(::DivisibleBy, {
      ensure((it % n.toInt() == 0) to msg(it))
    })
}