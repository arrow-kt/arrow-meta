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

interface Tuple11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K> : Tuple10<A, B, C, D, E, F, G, H, I, J> {
  operator fun component11(): K = value[10] as K
}

interface Tuple12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L> : Tuple11<A, B, C, D, E, F, G, H, I, J, K> {
  operator fun component12(): L = value[11] as L
}

interface Tuple13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M> : Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> {
  operator fun component13(): M = value[12] as M
}

interface Tuple14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N> : Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> {
  operator fun component14(): N = value[13] as N
}

interface Tuple15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O> : Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> {
  operator fun component15(): O = value[14] as O
}

interface Tuple16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P> : Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> {
  operator fun component16(): P = value[15] as P
}

interface Tuple17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q> : Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {
  operator fun component17(): Q = value[16] as Q
}

interface Tuple18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R> : Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> {
  operator fun component18(): R = value[17] as R
}

interface Tuple19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S> : Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> {
  operator fun component19(): S = value[18] as S
}

interface Tuple20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T> : Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> {
  operator fun component20(): T = value[19] as T
}

interface Tuple21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U> : Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> {
  operator fun component21(): U = value[20] as U
}

interface Tuple22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V> : Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> {
  operator fun component22(): V = value[21] as V
}

inline class tupleOf private constructor(override val value: Array<Any?>) :
  Tuple22<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing> {
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

    operator fun <A, B, C, D, E, F, G, H, I, J, K> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K): Tuple11<A, B, C, D, E, F, G, H, I, J, K> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L): Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M): Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N): Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O): Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P): Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q): Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R): Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S): Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T): Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U): Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u))

    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V): Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =
      tupleOf(arrayOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v))
  }
}
