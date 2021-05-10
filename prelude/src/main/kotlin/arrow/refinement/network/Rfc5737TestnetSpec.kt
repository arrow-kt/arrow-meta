package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.or

@JvmInline
value class Rfc5737TestnetSpec private constructor(val value: String) {
  companion object : Refined<String, Rfc5737TestnetSpec>(
    ::Rfc5737TestnetSpec, Rfc5737Testnet1Spec or Rfc5737Testnet2Spec or Rfc5737Testnet3Spec
  )
}