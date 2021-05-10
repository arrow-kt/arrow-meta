package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.or

@JvmInline
value class Rfc1918PrivateSpec private constructor(val value: String) {
  companion object : Refined<String, Rfc1918PrivateSpec>(
    ::Rfc1918PrivateSpec, Rfc1918ClassAPrivateSpec or Rfc1918ClassBPrivateSpec or Rfc1918ClassCPrivateSpec
  )
}