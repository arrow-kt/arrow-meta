package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.or

@JvmInline
value class Rfc1918PrivateNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc1918PrivateNetwork>(
    ::Rfc1918PrivateNetwork, Rfc1918ClassAPrivateNetwork or Rfc1918ClassBPrivateNetwork or Rfc1918ClassCPrivateNetwork
  )
}