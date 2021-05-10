package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Tail<A> private constructor(val value: Iterable<A>) {
  class Element<A>(value: Iterable<A>, msg: (Iterable<*>) -> String? = { null }) :
    Refined<Iterable<A>, Tail<A>>(::Tail, {
      ensure((it.drop(1) == value) to (msg(it) ?: "Expected tail: [${it.drop(1)}] to be $value"))
    })
}