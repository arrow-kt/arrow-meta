package arrow.refinement.collections

import arrow.refinement.Refined

@JvmInline
value class Empty private constructor(val value: Iterable<*>) {
  companion object : Refined<Iterable<*>, Empty>(::Empty, Count.N(0u) {
    "Expected empty iterable but found $it"
  })
}