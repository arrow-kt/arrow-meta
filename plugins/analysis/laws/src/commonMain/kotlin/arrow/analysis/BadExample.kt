package arrow

import arrow.analysis.Law
import arrow.analysis.pre

@Law
fun Int.safeDiv(other: Int): Int {
  pre(other != 0) { "other is not zero" }
  return this / other
}

fun foo() {
  // uncomment code below to fail gradle build
  // val result = 1 / 0
}
