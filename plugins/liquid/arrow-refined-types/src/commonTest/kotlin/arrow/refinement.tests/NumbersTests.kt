package arrow.refinement.tests

import arrow.refinement.numbers.DivisibleBy
import arrow.refinement.numbers.Even
import arrow.refinement.numbers.From
import arrow.refinement.numbers.FromTo
import arrow.refinement.numbers.GreaterThan
import arrow.refinement.numbers.LessThan
import arrow.refinement.numbers.NegativeInt
import arrow.refinement.numbers.NotZero
import arrow.refinement.numbers.Odd
import arrow.refinement.numbers.PositiveInt
import arrow.refinement.numbers.To
import arrow.refinement.numbers.Zero
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class NumbersTests :
  RefinedLaws<Int>(
    Arb.int(),
    DivisibleBy.N(2u),
    Even,
    From.N(0u),
    FromTo.N(0u, 10u),
    GreaterThan.N(0u),
    LessThan.N(0u),
    NegativeInt,
    NotZero,
    Odd,
    PositiveInt,
    To.N(10u),
    Zero
  )

