@file:Suppress("UNCHECKED_CAST")

package arrow

import arrow.Proof
import arrow.TypeProof

/**
 * Unions are commutative
 * Union2<A, B> =:= Union2<B, A>
 */
@Proof(TypeProof.Subtyping)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
  Union22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.commutative():
  Union22<V, U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A> =
  this as Union22<V, U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>