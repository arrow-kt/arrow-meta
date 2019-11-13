package arrow

interface Union4<out A, out B, out C, out D> {
  val value: Any?
}
typealias Union3<A, B, C> = Union4<A, B, C, Nothing>
typealias Union2<A, B> = Union3<A, B, Nothing>

inline class Union(override val value: Any?) : Union4<Nothing, Nothing, Nothing, Nothing>

@proof(implicitConversion = true)
fun <A> A.first(): Union4<A, Nothing, Nothing, Nothing> =
  Union(this)

@proof(implicitConversion = true)
fun <A> A.second(): Union4<Nothing, A, Nothing, Nothing> =
  Union(this)

@proof(implicitConversion = true)
fun <A> A.third(): Union4<Nothing, Nothing, A, Nothing> =
  Union(this)

@proof(implicitConversion = true)
fun <A> A.fourth(): Union4<Nothing, Nothing, Nothing, A> =
  Union(this)

@proof(implicitConversion = true)
inline fun <reified A> Union4<A, Nothing, Nothing, Nothing>.firstN(): A? {
  val result: A? = (this as Union).value?.let { if (it is A) it else null }
  println("firstN: ${A::class.java}: value: $result")
  return result
}

@proof(implicitConversion = true)
inline fun <reified A> Union4<Nothing, A, Nothing, Nothing>.secondN(): A? =
  (this as Union).value?.let { if (it is A) it else null }

@proof(implicitConversion = true)
inline fun <reified A> Union4<Nothing, Nothing, A, Nothing>.thirdN(): A? =
  (this as Union).value?.let { if (it is A) it else null }

@proof(implicitConversion = true)
inline fun <reified A> Union4<Nothing, Nothing, Nothing, A>.fourthN(): A? =
  (this as Union).value?.let { if (it is A) it else null }