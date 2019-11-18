package arrowx

import arrow.proof

interface Tuple22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V> {
  val value: Any?
  operator fun component1(): A
  operator fun component2(): B
  operator fun component3(): C
  operator fun component4(): D
  operator fun component5(): E
  operator fun component6(): F
  operator fun component7(): G
  operator fun component8(): H
  operator fun component9(): I
  operator fun component10(): J
  operator fun component11(): K
  operator fun component12(): L
  operator fun component13(): M
  operator fun component14(): N
  operator fun component15(): O
  operator fun component16(): P
  operator fun component17(): Q
  operator fun component18(): R
  operator fun component19(): S
  operator fun component20(): T
  operator fun component21(): U
  operator fun component22(): V
}

typealias Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Impossible>
typealias Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Impossible>
typealias Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Impossible>
typealias Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Impossible>
typealias Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Impossible>
typealias Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Impossible>
typealias Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Impossible>
typealias Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, Impossible>
typealias Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> = Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, Impossible>
typealias Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> = Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, Impossible>
typealias Tuple11<A, B, C, D, E, F, G, H, I, J, K> = Tuple12<A, B, C, D, E, F, G, H, I, J, K, Impossible>
typealias Tuple10<A, B, C, D, E, F, G, H, I, J> = Tuple11<A, B, C, D, E, F, G, H, I, J, Impossible>
typealias Tuple9<A, B, C, D, E, F, G, H, I> = Tuple10<A, B, C, D, E, F, G, H, I, Impossible>
typealias Tuple8<A, B, C, D, E, F, G, H> = Tuple9<A, B, C, D, E, F, G, H, Impossible>
typealias Tuple7<A, B, C, D, E, F, G> = Tuple8<A, B, C, D, E, F, G, Impossible>
typealias Tuple6<A, B, C, D, E, F> = Tuple7<A, B, C, D, E, F, Impossible>
typealias Tuple5<A, B, C, D, E> = Tuple6<A, B, C, D, E, Impossible>
typealias Tuple4<A, B, C, D> = Tuple5<A, B, C, D, Impossible>
typealias Tuple3<A, B, C> = Tuple4<A, B, C, Impossible>
typealias Tuple2<A, B> = Tuple3<A, B, Impossible>

inline class Tupled(override val value: Array<Any?>) : Tuple22<Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?> {
  override fun component1(): Any? = value[0]
  override fun component2(): Any? = value[1]
  override fun component3(): Any? = value[2]
  override fun component4(): Any? = value[3]
  override fun component5(): Any? = value[4]
  override fun component6(): Any? = value[5]
  override fun component7(): Any? = value[6]
  override fun component8(): Any? = value[7]
  override fun component9(): Any? = value[8]
  override fun component10(): Any? = value[9]
  override fun component11(): Any? = value[10]
  override fun component12(): Any? = value[11]
  override fun component13(): Any? = value[12]
  override fun component14(): Any? = value[13]
  override fun component15(): Any? = value[14]
  override fun component16(): Any? = value[15]
  override fun component17(): Any? = value[16]
  override fun component18(): Any? = value[17]
  override fun component19(): Any? = value[18]
  override fun component20(): Any? = value[19]
  override fun component21(): Any? = value[20]
  override fun component22(): Any? = value[21]
  @Suppress("UNCHECKED_CAST")
  inline fun widen(): Tuple22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing> =
    Tupled(value) as Tuple22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
}

data class Person(val name: String, val age: Int)

@proof(conversion = true)
fun Person.tupled(): Tuple2<String, Int> =
  Tupled(arrayOf(name, age)).widen()

@proof(conversion = true)
fun Tuple2<String, Int>.person(): Person {
  val (name, age) = this
  return Person(name, age)
}