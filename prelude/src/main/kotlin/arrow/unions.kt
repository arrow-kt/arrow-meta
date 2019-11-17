package arrow

class Impossible

interface Union4<out A, out B, out C, out D> {
  val value: Any?
}
typealias Union3<A, B, C> = Union4<A, B, C, Impossible>
typealias Union2<A, B> = Union3<A, B, Impossible>

inline class Union(override val value: Any?) : Union4<Nothing, Nothing, Nothing, Nothing>

@proof(conversion = true)
fun <A> A.first(): Union4<A, Any?, Any?, Any?> =
  Union(this)

@proof(conversion = true)
fun <A> A.second(): Union4<Any?, A, Any?, Any?> =
  Union(this)

@proof(conversion = true)
fun <A> A.third(): Union4<Any?, Any?, A, Any?> =
  Union(this)

@proof(conversion = true)
fun <A> A.fourth(): Union4<Any?, Any?, Any?, A> =
  Union(this)

@proof(conversion = true)
inline fun <reified A> Union4<A, Any?, Any?, Any?>.firstN(): A? =
  (this as Union).value as? A

@proof(conversion = true)
inline fun <reified A> Union4<Any?, A, Any?, Any?>.secondN(): A? =
  (this as Union).value as? A

@proof(conversion = true)
inline fun <reified A> Union4<Any?, Any?, A, Any?>.thirdN(): A? =
  (this as Union).value as? A

@proof(conversion = true)
inline fun <reified A> Union4<Any?, Any?, Any?, A>.fourthN(): A? =
  (this as Union).value as? A