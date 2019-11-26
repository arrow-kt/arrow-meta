package arrow

import arrow.TypeProof.*

class Impossible

interface Union4<out A, out B, out C, out D> {
  val value: Any?
}
typealias Union3<A, B, C> = Union4<A, B, C, Impossible>
typealias Union2<A, B> = Union3<A, B, Impossible>

inline class Union(override val value: Any?) : Union4<Nothing, Nothing, Nothing, Nothing>

// val x: Union<String, Int> = 0.0
@Proof(of = [Subtyping])
inline fun <A> A.first(): Union4<A, Any?, Any?, Any?> =
  Union(this)

@Proof(of = [Subtyping])
inline fun <A> A.second(): Union4<Any?, A, Any?, Any?> =
  Union(this)

@Proof(of = [Subtyping])
inline fun <A> A.third(): Union4<Any?, Any?, A, Any?> =
  Union(this)

@Proof(of = [Subtyping])
inline fun <A> A.fourth(): Union4<Any?, Any?, Any?, A> =
  Union(this)

@Proof(of = [Subtyping])
inline fun <reified A> Union4<A, Any?, Any?, Any?>.firstN(): A? =
  (this as Union).value as? A

@Proof(of = [Subtyping])
inline fun <reified A> Union4<Any?, A, Any?, Any?>.secondN(): A? =
  (this as Union).value as? A

@Proof(of = [Subtyping])
inline fun <reified A> Union4<Any?, Any?, A, Any?>.thirdN(): A? =
  (this as Union).value as? A

@Proof(of = [Subtyping])
inline fun <reified A> Union4<Any?, Any?, Any?, A>.fourthN(): A? =
  (this as Union).value as? A