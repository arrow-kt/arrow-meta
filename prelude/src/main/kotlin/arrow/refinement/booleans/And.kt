package arrow.refinement.booleans

import arrow.refinement.Refined

/**
 * Boolean conjunction of the [left] and [right] predicates.
 * left && right
 */
class And<A, B>(
  private val left: Refined<A, *>,
  private val right: Refined<A, B>,
  defaultMsg: String? = null
) :
  Refined<A, B>({ right(it) }, {
    val newConstrains = left.constraints(it) + right.constraints(it)
    newConstrains.mapValues { (_, v) -> defaultMsg ?: v }
  })