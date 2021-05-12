package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class LessThan private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be less than $n" }) :
    Refined<Int, LessThan>(::LessThan, {
      ensure((it < n.toInt()) to msg(it))
    })
}