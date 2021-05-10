package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Contains<A> private constructor(val value: Iterable<A>) {
  class Element<A>(value: A, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<A>, Contains<A>>(::Contains, {
    ensure((value in it) to (msg(it) ?: "Expected $it to contain $value"))
  })
}