package arrow.refinement.booleans

import arrow.refinement.Refined

/**
 * Boolean negation of [predicate].
 * !predicate
 *
 * @see arrow.refinement.Refined.not
 */
class Not<A, B>(private val predicate: Refined<A, B>, defaultMsg: (A) -> String? = { null }) :
  Refined<A, B>(predicate.f, {
    predicate.constraints(it).map { (passed, msg) ->
      !passed to (defaultMsg(it) ?: msg)
    }
  })