package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class First<A> private constructor(val value: Iterable<A>) {
  class Element<A>(value: A, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<A>, First<A>>(::First, {
    ensure((it.firstOrNull() == value) to (msg(it) ?: "Expected first: [${it.firstOrNull()}] to be $value"))
  })
}