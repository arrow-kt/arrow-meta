package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class MaxSize private constructor(val value: Iterable<*>) {
  class N(value: UInt, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<*>, MaxSize>(::MaxSize, {
    ensure((it.count() <= value.toInt()) to (msg(it) ?: "Expected max size of $value but found ${it.count()}"))
  })
}