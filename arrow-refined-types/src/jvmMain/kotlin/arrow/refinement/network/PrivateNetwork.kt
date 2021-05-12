package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.or

@JvmInline
value class PrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, PrivateNetwork>(
    ::PrivateNetwork, Rfc1918PrivateNetwork or Rfc5737TestnetNetwork or Rfc3927LocalLinkNetwork or Rfc2544BenchmarkNetwork
  )
}
