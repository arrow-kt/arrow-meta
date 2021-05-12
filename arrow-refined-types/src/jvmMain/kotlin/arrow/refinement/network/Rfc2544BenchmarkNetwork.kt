package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.and
import arrow.refinement.or
import arrow.refinement.strings.IPv4
import arrow.refinement.strings.StartsWith

@JvmInline
value class Rfc2544BenchmarkNetwork private constructor(val value: String) {
  companion object : Refined<String, Rfc2544BenchmarkNetwork>(
    ::Rfc2544BenchmarkNetwork, IPv4 and (StartsWith.Value("198.18.") or StartsWith.Value("198.19."))
  )
}