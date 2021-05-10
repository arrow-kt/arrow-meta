package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.and
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.MatchesRegex

@JvmInline
value class Rfc1918ClassBPrivateSpec private constructor(val value: String) {
  companion object : Refined<String, Rfc1918ClassBPrivateSpec>(
    ::Rfc1918ClassBPrivateSpec,
    IPv4 and MatchesRegex.Regex("^172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\..+".toRegex())
  )
}