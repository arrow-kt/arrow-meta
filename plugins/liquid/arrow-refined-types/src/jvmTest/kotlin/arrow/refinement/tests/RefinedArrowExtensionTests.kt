package arrow.refinement.tests

import arrow.core.invalid
import arrow.core.left
import arrow.core.right
import arrow.core.valid
import arrow.refinement.numbers.PositiveInt
import arrow.refinement.toEither
import arrow.refinement.toValidated
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RefinedArrowExtensionTests : StringSpec({

    "Either right with 1 as PositiveInt" {
        val one = PositiveInt.toEither(1)
        one shouldBe PositiveInt(1).right()
    }

    "Either left with -1 as PositiveInt" {
        val minusOne = PositiveInt.toEither(-1)
        minusOne shouldBe "-1 should be > 0".left()
    }

    "Valid with 1 as PositiveInt" {
        val one = PositiveInt.toValidated(1)
        one shouldBe PositiveInt(1).valid()
    }

    "InValid with -1 as PositiveInt" {
        val minusOne = PositiveInt.toValidated(-1)
        minusOne shouldBe "-1 should be > 0".invalid()
    }
})
