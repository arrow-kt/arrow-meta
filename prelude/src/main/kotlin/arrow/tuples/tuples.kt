@file:Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS", "UNCHECKED_CAST")

package arrow.tuples

import arrow.Proof
import arrow.TypeProof
import arrow.`🔥`
import arrow.`🚫`
import arrowx.Monoid
import arrowx.given

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

interface Tuple1<out A> : TupleN {
  operator fun component1(): A = value[0] as A
}

interface Tuple2<out A, out B> : Tuple1<A> {
  operator fun component2(): B = value[1] as B
}

interface Tuple3<out A, out B, out C> : Tuple2<A, B> {
  operator fun component3(): C = value[2] as C
}

interface Tuple4<out A, out B, out C, out D> : Tuple3<A, B, C> {
  operator fun component4(): D = value[3] as D
}

inline class tupleOf private constructor(override val value: Array<Any?>) : Tuple4<Nothing, Nothing, Nothing, Nothing> {
  companion object {
    operator fun <A, B> invoke(a: A, b: B): Tuple2<A, B> =
      tupleOf(arrayOf(a, b))

    operator fun <A, B, C> invoke(a: A, b: B, c: C): Tuple3<A, B, C> =
      tupleOf(arrayOf(a, b, c))

    operator fun <A, B, C, D> invoke(a: A, b: B, c: C, d: D): Tuple4<A, B, C, D> =
      tupleOf(arrayOf(a, b, c, d))
  }
}
