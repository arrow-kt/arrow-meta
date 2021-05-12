package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class MinSize private constructor(val value: Iterable<*>) {
  class N(value: UInt, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<*>, MinSize>(::MinSize, {
    ensure((it.count() >= value.toInt()) to (msg(it) ?: "Expected min size of $value but found ${it.count()}"))
  })
}