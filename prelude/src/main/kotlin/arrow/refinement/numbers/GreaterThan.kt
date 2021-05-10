package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class GreaterThan private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be greater than $n" }) :
    Refined<Int, GreaterThan>(::GreaterThan, {
      ensure((it > n.toInt()) to msg(it))
    })
}