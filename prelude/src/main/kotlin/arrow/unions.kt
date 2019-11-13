package arrow

interface UnionSyntax {
  val value: Any?
}

interface Union2<out A, out B> : UnionSyntax
interface Union3<out A, out B, out C> : Union2<A, B>
interface Union4<out A, out B, out C, out D> : Union3<A, B, C>

inline class Union(override val value: Any?) : Union4<Nothing, Nothing, Nothing, Nothing>

@proof(implicitConversion = true)
fun <A> A.first(): Union2<A, Nothing> =
  Union(this)

@proof(implicitConversion = true)
fun <A> A.second(): Union2<Nothing, A> =
  Union(this)

@proof(implicitConversion = true)
fun <A> A.third(): Union3<Nothing, Nothing, A> =
  Union(this)

///**
// * Unions are commutative
// * Union2<A, B> =:= Union2<B, A>
// */
//@proof(implicitConversion = true)
//fun <A, B> Union2<A, B>.commutative(): Union2<B, A> =
//  this as Union2<B, A>
//
///**
// * Unions are associative
// * Union2<A, Union2<B, C>> =:= Union2<Union2<A, B>, C>
// */
//@proof(implicitConversion = true)
//fun <A, B, C> Union2<A, Union2<B, C>>.associative(): Union2<Union2<A, B>, C> =
//  this as Union2<Union2<A, B>, C>

/**
 * Unions are subtypes of their nullable types
 * Union2<A, null> =:= A?
 */
@proof(implicitConversion = true)
fun <A> Union2<A, Nothing>.first(): A? =
  (this as Union).value as? A

/**
 * Unions are subtypes of their nullable types
 * Union2<null, B> =:= B?
 */
@proof(implicitConversion = true)
fun <A> Union2<Nothing, A>.second(): A? =
  (this as Union).value as? A
