@file:Suppress("UNCHECKED_CAST")

package arrow

/**
 * Unions are commutative
 * Union2<A, B> =:= Union2<B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B> Union2<A, B>.commutative2(): Union2<B, A> = this as Union2<B, A>

/**
 * Unions are commutative
 * Union3<A, B, C> =:= Union3<C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C> Union3<A, B, C>.commutative3(): Union3<C, B, A> = this as Union3<C, B, A>

/**
 * Unions are commutative
 * Union4<A, B, C, D> =:= Union4<D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D> Union4<A, B, C, D>.commutative4(): Union4<D, C, B, A> = this as Union4<D, C, B, A>

/**
 * Unions are commutative
 * Union5<A, B, C, D, E> =:= Union5<E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E> Union5<A, B, C, D, E>.commutative5(): Union5<E, D, C, B, A> = this as Union5<E, D, C, B, A>

/**
 * Unions are commutative
 * Union6<A, B, C, D, E, F> =:= Union6<F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F> Union6<A, B, C, D, E, F>.commutative6(): Union6<F, E, D, C, B, A> = this as Union6<F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union7<A, B, C, D, E, F, G> =:= Union7<G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G> Union7<A, B, C, D, E, F, G>.commutative7(): Union7<G, F, E, D, C, B, A> = this as Union7<G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union8<A, B, C, D, E, F, G, H> =:= Union8<H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H> Union8<A, B, C, D, E, F, G, H>.commutative8(): Union8<H, G, F, E, D, C, B, A> = this as Union8<H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union9<A, B, C, D, E, F, G, H, I> =:= Union9<I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I> Union9<A, B, C, D, E, F, G, H, I>.commutative9(): Union9<I, H, G, F, E, D, C, B, A>
  = this as Union9<I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union10<A, B, C, D, E, F, G, H, I, J> =:= Union10<J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J> Union10<A, B, C, D, E, F, G, H, I, J>.commutative10(): Union10<J, I, H, G, F, E, D, C, B, A>
  = this as Union10<J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union11<A, B, C, D, E, F, G, H, I, J, K> =:= Union11<K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K> Union11<A, B, C, D, E, F, G, H, I, J, K>.commutative11(): Union11<K, J, I, H, G, F, E, D, C, B, A>
  = this as Union11<K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union12<A, B, C, D, E, F, G, H, I, J, K, L> =:= Union12<L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L> Union12<A, B, C, D, E, F, G, H, I, J, K, L>.commutative12(): Union12<L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union12<L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union13<A, B, C, D, E, F, G, H, I, J, K, L, M> =:= Union13<M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Union13<A, B, C, D, E, F, G, H, I, J, K, L, M>.commutative13(): Union13<M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union13<M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> =:= Union14<N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Union14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.commutative14(): Union14<N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union14<N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> =:= Union15<O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Union15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.commutative15(): Union15<O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union15<O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> =:= Union16<P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Union16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.commutative16(): Union16<P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union16<P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> =:= Union17<Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Union17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.commutative17(): Union17<Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union17<Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> =:= Union18<R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Union18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.commutative18(): Union18<R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union18<R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> =:= Union19<S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Union19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.commutative19(): Union19<S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union19<S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =:= Union20<T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Union20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.commutative20(): Union20<T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 = this as Union20<T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =:= Union21<U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Union21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.commutative21(): Union21<U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union21<U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>

/**
 * Unions are commutative
 * Union22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =:= Union22<V, U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
 */
@Proof(TypeProof.Extension, coerce = true)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Union22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.commutative22(): Union22<V, U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>
  = this as Union22<V, U, T, S, R, Q, P, O, N, M, L, K, J, I, H, G, F, E, D, C, B, A>