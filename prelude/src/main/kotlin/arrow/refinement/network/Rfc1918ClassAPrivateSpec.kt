package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.and
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith

@JvmInline
value class Rfc1918ClassAPrivateSpec private constructor(val value: String) {
  companion object : Refined<String, Rfc1918ClassAPrivateSpec>(
    ::Rfc1918ClassAPrivateSpec, IPv4 and StartsWith.Value("10.")
  )
}