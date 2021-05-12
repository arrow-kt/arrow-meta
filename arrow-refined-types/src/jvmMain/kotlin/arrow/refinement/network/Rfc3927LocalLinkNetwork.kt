package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.and
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith

@JvmInline
value class Rfc3927LocalLinkNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc3927LocalLinkNetwork>(
    ::Rfc3927LocalLinkNetwork, IPv4 and StartsWith.Value("169.254.")
  )
}