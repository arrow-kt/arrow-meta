package arrowx

import arrow.proof

interface Tuple4<out A, out B, out C, out D> {
  val value: Any?
  operator fun component1(): A
  operator fun component2(): B
  operator fun component3(): C
  operator fun component4(): D
}

typealias Tuple3<A, B, C> = Tuple4<A, B, C, Impossible>
typealias Tuple2<A, B> = Tuple3<A, B, Impossible>

inline class Tupled(override val value: Array<Any?>) : Tuple4<Any?, Any?, Any?, Any?> {
  override fun component1(): Any? = value[0]
  override fun component2(): Any? = value[1]
  override fun component3(): Any? = value[2]
  override fun component4(): Any? = value[3]
  inline fun <A, B, C, D> widden(): Tuple4<A, B, C, D> = Tupled(value) as Tuple4<A, B, C, D>
}

data class Person(val name: String, val age: Int)

@proof(conversion = true)
fun Person.tupled(): Tuple2<String, Int> =
  Tupled(arrayOf(name, age)).widden()

@proof(conversion = true)
fun Tuple2<String, Int>.person(): Person {
  val (name, age) = this
  return Person(name, age)
}