package arrow.refinement.tests

import arrow.refinement.chars.Digit
import arrow.refinement.chars.Letter
import arrow.refinement.chars.LetterOrDigit
import arrow.refinement.chars.LowerCaseChar
import arrow.refinement.chars.UpperCaseChar
import arrow.refinement.chars.WhiteSpaceChar
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char

class CharsTests :
  RefinedLaws<Char>(
    Arb.char(),
    Digit,
    Letter,
    LetterOrDigit,
    LowerCaseChar,
    UpperCaseChar,
    WhiteSpaceChar
  )
