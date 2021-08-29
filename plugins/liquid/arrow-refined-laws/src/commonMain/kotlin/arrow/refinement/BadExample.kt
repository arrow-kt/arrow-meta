package arrow

import arrow.refinement.Law
import arrow.refinement.pre

@Law
fun Int.safeDiv(other: Int): Int {
  pre(other != 0) { "other is not zero" }
  return this / other
}

fun foo() {
  // uncomment code below to fail gradle build
  // val result = 1 / 0
}
