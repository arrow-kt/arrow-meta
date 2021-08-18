package test

import arrow.refinement.Law
import arrow.refinement.pre

@Law
fun Int.safeDiv(other: Int): Int {
  pre("other is not zero") { other != 0 }
  return this / other
}

fun foo() {
  //uncommenting this will cause the build to fail
  //val result = 1 / 0
}

