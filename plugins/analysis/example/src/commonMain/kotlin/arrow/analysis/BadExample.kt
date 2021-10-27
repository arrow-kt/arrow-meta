package arrow.analysis

// uncomment code to fail gradle build
// fun bar(x: Int): Int = 1 / x
fun bar(x: Int): Int {
  pre(x > 0) { "x positive" }
  return 1 / x
}
// val other: Int = emptyList<Int>().get(1)

fun increment(x: Int): Int {
  pre(x > 0) { "value must be positive" }
  return (x + 1).post({ it > 0 }) { "result is positive" }
}
val example = increment(increment(1))

class Positive(val value: Int) {
  init { require(value > 0) }
}
val positiveExample = Positive(1)
fun Positive.add(other: Positive) =
  Positive(this.value + other.value)
