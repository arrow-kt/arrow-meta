package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.and
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith

@JvmInline
value class Rfc5737Testnet1Network private constructor(val value: String) {
  companion object : Refined<String, Rfc5737Testnet1Network>(
    ::Rfc5737Testnet1Network, IPv4 and StartsWith.Value("192.0.2.")
  )
}