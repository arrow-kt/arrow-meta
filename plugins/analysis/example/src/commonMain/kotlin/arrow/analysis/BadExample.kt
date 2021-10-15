package arrow.analysis

// uncomment code to fail gradle build
// fun bar(x: Int): Int = 1 / x
fun bar(x: Int): Int {
  pre(x > 0) { "x positive" }
  return 1 / x
}
// val other: Int = emptyList<Int>().get(1)
