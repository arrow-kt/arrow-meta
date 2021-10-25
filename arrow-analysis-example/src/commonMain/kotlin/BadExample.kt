package test

import arrow.analysis.pre

// uncomment code to fail gradle build
// fun bar(x: Int): Int = 1 / x
fun bar(x: Int): Int {
  pre(x > 0) { "x positive" }
  return 1 / 0
}
 val other: Int = emptyList<Int>().get(1)
