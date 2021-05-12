package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Equal
import arrow.refinement.or

@JvmInline
value class From private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be greater than or equal to $n" }) :
    Refined<Int, From>(::From, GreaterThan.N(n) or Equal.Value(n.toInt()))
}