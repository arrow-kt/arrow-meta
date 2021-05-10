package arrow.refinement.booleans

import arrow.refinement.Refined
import arrow.refinement.allValid

/**
 * Boolean disjunction of the [left] and [right] predicates.
 * left || right
 */
class Or<A, B>(
  private val left: Refined<A, *>,
  private val right: Refined<A, B>,
  defaultMsg: String? = null
) :
  Refined<A, B>({ right(it) }, {
    val newConstrains =
      if (left.constraints(it).allValid()) left.constraints(it)
      else right.constraints(it)
    newConstrains.mapValues { (_, v) -> defaultMsg ?: v }
  })