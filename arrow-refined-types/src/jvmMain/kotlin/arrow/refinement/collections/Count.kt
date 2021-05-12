package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Count private constructor(val value: Iterable<*>) {
  class N(value: UInt, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<*>, Count>(::Count, {
    ensure((it.count() == value.toInt()) to (msg(it) ?: "Expected count to be $value"))
  })
}