@file:Suppress("UNCHECKED_CAST")

package arrow

interface Union2<out A, out B>
interface Union3<out A, out B, out C> : Union2<A, B>
interface Union4<out A, out B, out C, out D> : Union3<A, B, C>

private inline class U(val value: Array<Any?>) :
  Union2<Nothing, Nothing>,
  Union3<Nothing, Nothing, Nothing>,
  Union4<Nothing, Nothing, Nothing, Nothing> {

  inline operator fun <reified A> invoke(): A? =
    value.filterIsInstance<A>().firstOrNull()

  companion object {
    fun <A, B> first(a: A): Union2<A, B> = U(arrayOf(a, null))
    fun <A, B> second(b: B): Union2<A, B> = U(arrayOf(null, b))
    fun <A, B, C> third(c: C): Union3<A, B, C> = U(arrayOf(null, null, c))
    fun <A, B, C, D> fourth(d: D): Union4<A, B, C, D> = U(arrayOf(null, null, null, d))
  }
}

//typealias Result = Union2<String, Int>
//
//object test {
//  val a: Result = "a"
//  val b: Result = 1
//}