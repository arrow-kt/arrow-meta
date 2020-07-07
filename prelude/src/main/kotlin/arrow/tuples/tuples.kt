@file:Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS", "UNCHECKED_CAST")

package arrow.tuples

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

interface Tuple5<out A, out B, out C, out D, out E> : Tuple4<A, B, C, D> {
  operator fun component5(): E = value[4] as E
}

interface Tuple6<out A, out B, out C, out D, out E, out F> : Tuple5<A, B, C, D, E> {
  operator fun component6(): F = value[5] as F
}

interface Tuple7<out A, out B, out C, out D, out E, out F, out G> : Tuple6<A, B, C, D, E, F> {
  operator fun component7(): G = value[6] as G
}

interface Tuple8<out A, out B, out C, out D, out E, out F, out G, out H> : Tuple7<A, B, C, D, E, F, G> {
  operator fun component8(): H = value[7] as H
}

interface Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I> : Tuple8<A, B, C, D, E, F, G, H> {
  operator fun component9(): I = value[8] as I
}

interface Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J> : Tuple9<A, B, C, D, E, F, G, H, I> {
  operator fun component10(): J = value[9] as J
}

inline class tupleOf private constructor(override val value: Array<Any?>) :
  Tuple10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing> {
  companion object {
    operator fun <A, B> invoke(a: A, b: B): Tuple2<A, B> =
      tupleOf(arrayOf(a, b))

    operator fun <A, B, C> invoke(a: A, b: B, c: C): Tuple3<A, B, C> =
      tupleOf(arrayOf(a, b, c))

    operator fun <A, B, C, D> invoke(a: A, b: B, c: C, d: D): Tuple4<A, B, C, D> =
      tupleOf(arrayOf(a, b, c, d))

    operator fun <A, B, C, D, E> invoke(a: A, b: B, c: C, d: D, e: E): Tuple5<A, B, C, D, E> =
      tupleOf(arrayOf(a, b, c, d, e))

    operator fun <A, B, C, D, E, F> invoke(a: A, b: B, c: C, d: D, e: E, f: F): Tuple6<A, B, C, D, E, F> =
      tupleOf(arrayOf(a, b, c, d, e, f))

    operator fun <A, B, C, D, E, F, G> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G): Tuple7<A, B, C, D, E, F, G> =
      tupleOf(arrayOf(a, b, c, d, e, f, g))

    operator fun <A, B, C, D, E, F, G, H> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H): Tuple8<A, B, C, D, E, F, G, H> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h))

    operator fun <A, B, C, D, E, F, G, H, I> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I): Tuple9<A, B, C, D, E, F, G, H, I> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i))

    operator fun <A, B, C, D, E, F, G, H, I, J> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J): Tuple10<A, B, C, D, E, F, G, H, I, J> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j))
  }
}
