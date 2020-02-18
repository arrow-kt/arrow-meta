package arrow.tuples

import arrow.`🔥`
import arrow.`🚫`

/**
 * Products                                   _              _
 *                                      _.(_)..__________(_)
 *                                   .''.'""""\             "".
 *                                  '-'/__     |_________..--..'
 *                                        '.   ||  /    |  /
 *                                          )  ;| (    (  (
 *                                         /  //   |    |__|___
 *                                        /   ||   |__.-'      '.
 *                                      .:    |L.-'"             \
 *                                     |                         |
 *                                     |            _            ;
 *                                     |      _.-'/"_'"         /
 *                                     `..__.T    ''           /
 *                                           |                /
 *                                           |               /ASCII
 *                                            '.            /Skate-
 *                                              )--._      /boarding
 *                                            .'     \    /Vert grab
 *                                           /        ;  /Lauri Kangas 12/99
 *                                           |       / .'
 *                                            '.__.-' /
 *                                                /   |
 *                                               /   /
 *                                              /   /
 *                                             /  .'
 *                               .__          /  /
 *                                   "'-._  _/  /lka
 *                                        ./   /-.
 *                                         /// */
interface TupleN {
  val value: Array<Any?>
}
typealias Tuple1<A> = Tuple2<A, `🚫`>
typealias Tuple2<A, B> = Tuple3<A, B, `🚫`>
typealias Tuple3<A, B, C> = Tuple4<A, B, C, `🚫`>
typealias Tuple4<A, B, C, D> = Tuple5<A, B, C, D, `🚫`>
typealias Tuple5<A, B, C, D, E> = Tuple6<A, B, C, D, E, `🚫`>
typealias Tuple6<A, B, C, D, E, F> = Tuple7<A, B, C, D, E, F, `🚫`>
typealias Tuple7<A, B, C, D, E, F, G> = Tuple8<A, B, C, D, E, F, G, `🚫`>
typealias Tuple8<A, B, C, D, E, F, G, H> = Tuple9<A, B, C, D, E, F, G, H, `🚫`>
typealias Tuple9<A, B, C, D, E, F, G, H, I> = Tuple10<A, B, C, D, E, F, G, H, I, `🚫`>
typealias Tuple10<A, B, C, D, E, F, G, H, I, J> = Tuple11<A, B, C, D, E, F, G, H, I, J, `🚫`>
typealias Tuple11<A, B, C, D, E, F, G, H, I, J, K> = Tuple12<A, B, C, D, E, F, G, H, I, J, K, `🚫`>
typealias Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> = Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, `🚫`>
typealias Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> = Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, `🚫`>
typealias Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, `🚫`>
typealias Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, `🚫`>
typealias Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, `🚫`>
typealias Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, `🚫`>
typealias Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, `🚫`>
typealias Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, `🚫`>
typealias Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, `🚫`>
typealias Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, `🚫`>

@Suppress("UNCHECKED_CAST")
interface Tuple22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V> : TupleN {
  operator fun component1(): A = value[0] as A
  operator fun component2(): B = value[0] as B
  operator fun component3(): C = value[0] as C
  operator fun component4(): D = value[0] as D
  operator fun component5(): E = value[0] as E
  operator fun component6(): F = value[0] as F
  operator fun component7(): G = value[0] as G
  operator fun component8(): H = value[0] as H
  operator fun component9(): I = value[0] as I
  operator fun component10(): J = value[0] as J
  operator fun component11(): K = value[0] as K
  operator fun component12(): L = value[0] as L
  operator fun component13(): M = value[0] as M
  operator fun component14(): N = value[0] as N
  operator fun component15(): O = value[0] as O
  operator fun component16(): P = value[0] as P
  operator fun component17(): Q = value[0] as Q
  operator fun component18(): R = value[0] as R
  operator fun component19(): S = value[0] as S
  operator fun component20(): T = value[0] as T
  operator fun component21(): U = value[0] as U
  operator fun component22(): V = value[0] as V
}

inline class Tupled(override val value: Array<Any?>) : Tuple22<`🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`, `🔥`>

fun <A, B> tupled(a: A, b: B): Tuple2<A, B> =
  Tupled(arrayOf(a, b))

fun <A, B, C> tupled(a: A, b: B, c: C): Tuple3<A, B, C> =
  Tupled(arrayOf(a, b, c))

fun <A, B, C, D> tupled(a: A, b: B, c: C, d: D): Tuple4<A, B, C, D> =
  Tupled(arrayOf(a, b, c, d))

fun <A, B, C, D, E> tupled(a: A, b: B, c: C, d: D, e: E): Tuple5<A, B, C, D, E> =
  Tupled(arrayOf(a, b, c, d, e))

fun <A, B, C, D, E, F> tupled(a: A, b: B, c: C, d: D, e: E, f: F): Tuple6<A, B, C, D, E, F> =
  Tupled(arrayOf(a, b, c, d, e, f))