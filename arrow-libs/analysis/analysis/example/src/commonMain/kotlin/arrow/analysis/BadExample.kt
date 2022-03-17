package arrow.analysis

import kotlin.jvm.JvmInline

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

@JvmInline
value class Positive(val value: Int) {
  init {
    require(value > 0)
  }
}

val positiveExample = Positive(1)

fun Positive.add(other: Positive) = Positive(this.value + other.value)

/*
fun nope(x: Int): Int {
  pre(x >= 0) { "value >= 0" }
  return (x + 1).post({ it > 1 }) { "result > 1" }
}

fun nope2(x: Int): Int {
  pre(x > 0) { "x must be positive" }
  return if (x < 0) 1 else 2
}
*/

// fun List<Int>.mappyWrong() = map { increment(it) }
fun List<Positive>.mappyOk() = map { increment(it.value) }

fun mappy(xs: List<Int>) = xs.mapNotNull { if (it > 0) Positive(it) else null }.mappyOk()

@JvmInline
value class NonEmptyList<A>(val value: List<A>) {
  init {
    require(value.isNotEmpty()) { "not empty" }
  }
}

fun <A> List<A>.myGet(index: Int): A {
  pre(index >= 0 && index < this.size) { "index within bounds" }
  return this.get(index)
}

fun <A> List<A>.firstOr(default: A): A = if (this.size > 0) this.myGet(0) else default

fun absoluteValue(n: Int): Int =
  when {
    n < 0 -> -n
    n == 0 -> 0
    else -> n
  }.post({ it >= 0 }) { "result >= 0" }

fun double(n: Int): Int {
  pre(n > 0) { "n positive" }
  val z = n + n
  val r = z + 1
  return r.post({ it > 0 }) { "result positive" }
}

fun double2(n: Int): Int {
  pre(n > 0) { "n positive" }
  return (n + n).let { it + 1 }.post({ it > 0 }) { "result positive" }
}

fun <A> List<A>.count(): Int {
  var count = 0.invariant({ it >= 0 }) { "z >= 0" }
  for (elt in this) {
    count = count + 1
  }
  return count.post({ it >= 0 }) { "result >= 0" }
}

fun <A> List<A>.isSingle() = all { it == first() }
