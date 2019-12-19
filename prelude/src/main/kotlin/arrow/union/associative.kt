@file:Suppress("UNCHECKED_CAST")

package arrow.union

import arrow.Proof
import arrow.TypeProof

/**
 * Unions are associative
 * Union2<A, Union2<B, C>> =:= Union2<Union2<A, B>, C>
 */
@Proof(TypeProof.Subtyping)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
  Union2<A, Union21<B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>>.associative():
  Union2<Union21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, V> =
  this as Union2<Union21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, V>

