package arrow.refinement.tests

import arrow.refinement.booleans.Equal
import arrow.refinement.booleans.Not
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.checkAll
import io.kotest.property.forAll

class BooleansTests : StringSpec({
  "And" {
    forAll(Arb.refinedChar(), Arb.refinedChar(), Arb.char()) { left, right, c ->
      (left and right).isValid(c) == (left.isValid(c) && right.isValid(c))
    }
    checkAll(Arb.refinedChar(), Arb.refinedChar(), Arb.char()) { left, right, c ->
      checkLaw(left and right, c, this)
    }
  }
  "Or" {
    forAll(Arb.refinedChar(), Arb.refinedChar(), Arb.char()) { left, right, c ->
      (left or right).isValid(c) == (left.isValid(c) || right.isValid(c))
    }
    checkAll(Arb.refinedChar(), Arb.refinedChar(), Arb.char()) { left, right, c ->
      checkLaw(left or right, c, this)
    }
  }
  "Equal" {
    forAll(Arb.char()) {
      Equal.Value(it).isValid(it)
    }
    checkAll(Arb.char()) { c ->
      checkLaw(Equal.Value(c), c, this)
    }
  }
  "Not" {
    forAll(Arb.char()) {
      !Not(Equal.Value(it)).isValid(it)
    }
    checkAll(Arb.char()) { c ->
      checkLaw(Not(Equal.Value(c)), c, this)
    }
  }
})
