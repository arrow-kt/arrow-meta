package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.or

@JvmInline
value class PrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, PrivateNetwork>(
    ::PrivateNetwork, Rfc1918PrivateSpec or Rfc5737TestnetSpec or Rfc3927LocalLinkSpec or Rfc2544BenchmarkSpec
  )
}
