package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.or

@JvmInline
value class Rfc5737TestnetNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc5737TestnetNetwork>(
    ::Rfc5737TestnetNetwork, Rfc5737Testnet1Network or Rfc5737Testnet2Network or Rfc5737Testnet3Network
  )
}