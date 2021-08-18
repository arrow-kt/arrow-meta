package arrow.refinement.tests

import arrow.refinement.Refined
import arrow.refinement.chars.Digit
import arrow.refinement.chars.Letter
import arrow.refinement.chars.LetterOrDigit
import arrow.refinement.chars.LowerCaseChar
import arrow.refinement.chars.UpperCaseChar
import arrow.refinement.chars.WhiteSpaceChar
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.PropertyContext
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.forAll

val refinedCharGen: Exhaustive<Refined<Char, *>> =
  exhaustive(listOf(Digit, Letter, LetterOrDigit, LowerCaseChar, UpperCaseChar, WhiteSpaceChar))

/**
 * Ensures require, isValid and orNull are consistent with each other
 */
suspend fun <A, B> Refined<A, B>.laws(arb: Arb<A>): PropertyContext =
  forAll(arb) {
    checkLaw(this@laws, it, this)
  }

fun <A, B> checkLaw(
  refined: Refined<A, B>,
  it: A, // for any value
  propertyContext: PropertyContext
) = if (refined.isValid(it)) { // if its valid
  refined.require(it) // require should not throw
  refined.orNull(it) != null // and orNull should return a non null value
} else try { // otherwise
  refined.require(it) // require will throw a IllegalArgumentException
  // this should never be reached
  throw IllegalStateException("$propertyContext require impl is inconsistent and does not throw on invalid values")
} catch (e: IllegalArgumentException) {
  refined.orNull(it) == null // if require is consistent orNull should return null
}

abstract class RefinedLaws<A>(arb: Arb<A>, vararg refined: Refined<A, *>) : StringSpec({
  println("Running laws for ${refined.joinToString { it::class.toString() }}")
  refined.forEach {
    it::class.simpleName?.invoke { it.laws(arb) }
  }
})
