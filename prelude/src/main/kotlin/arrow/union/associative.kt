@file:Suppress("UNCHECKED_CAST")

package arrow

/**
 * Unions are associative
 * Union2<A, Union2<B, C>> =:= Union2<Union2<A, B>, C>
 */
@Coercion
fun <A> A.associative(): Second<First<A>> =
  Union(this)

@Coercion
fun <A> SecondN<FirstN<A>>.flatten(): Second<A> =
  this as Union
