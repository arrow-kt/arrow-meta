package arrow.refinement.tests

import arrow.refinement.strings.EndsWith
import arrow.refinement.strings.HexString
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.IPv6
import arrow.refinement.strings.MatchesRegex
import arrow.refinement.strings.Size
import arrow.refinement.strings.StartsWith
import arrow.refinement.strings.ValidDouble
import arrow.refinement.strings.ValidInt
import arrow.refinement.strings.ValidLong
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

class StringsTests :
  RefinedLaws<String>(
    Arb.string(),
    "EndsWith.Value(\"a\")" to EndsWith.Value("a"),
    "HexString" to HexString,
    "IPv4" to IPv4,
    "IPv6" to IPv6,
    "MatchesRegex.Regex(\"(Arb.c)\".toRegex())" to MatchesRegex.Regex("(Arb.c)".toRegex()),
    "Size.N(0u)" to Size.N(0u),
    "StartsWith.Value(\"a\")" to StartsWith.Value("a"),
    "ValidDouble" to ValidDouble,
    "ValidInt" to ValidInt,
    "ValidLong" to ValidLong
  )
