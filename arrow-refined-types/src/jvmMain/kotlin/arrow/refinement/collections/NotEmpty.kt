package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.booleans.Not

@JvmInline
value class NotEmpty private constructor(val value: Iterable<*>) {
  companion object : Refined<Iterable<*>, NotEmpty>(::NotEmpty, Not(Empty) {
    "Expected non empty iterable but found $it"
  })
}