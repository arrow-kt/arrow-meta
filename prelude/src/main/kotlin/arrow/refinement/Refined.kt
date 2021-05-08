package arrow.refinement

typealias Constrains = Map<Boolean, String>

fun ensure(vararg constrains: Pair<Boolean, String>) : Map<Boolean, String> =
  mapOf(*constrains)

fun ensureA(vararg constrains: Any) : Map<Boolean, String> =
  constrains.filterIsInstance<Pair<Boolean, String>>().toMap() +
    constrains.filterIsInstance<Map<Boolean, String>>().fold(emptyMap(), Map<Boolean, String>::plus)

inline fun Constrains.allValid(): Boolean =
  all { it.key }

inline fun renderMessages(results: Constrains): String =
  results.entries.filter { !it.key }.joinToString { it.value }

fun require(constrains: Constrains): Unit {
  if (constrains.allValid()) Unit
  else throw java.lang.IllegalArgumentException(renderMessages(constrains))
}

inline fun <A> constrains(refined: Refined<A, *>, value: A): Constrains =
  refined.constrains(value)

abstract class Refined<A, B>(
  inline val f: (A) -> B,
  inline val constrains: (A) -> Constrains
) {

  constructor(f: (A) -> B, vararg predicates: Refined<A, *>) : this(f, { a: A ->
    predicates.map { it.constrains(a) }.fold(emptyMap<Boolean, String>()) { acc, constrains ->
      acc + constrains
    }
  })

  inline operator fun invoke(value: A): B {
    val results = constrains(value)
    return if (results.allValid()) f(value)
    else throw IllegalArgumentException(renderMessages(results))
  }

  inline fun orNull(value: A): B? =
    if (constrains(value).allValid()) f(value)
    else null

  inline operator fun <C> plus(other: Refined<B, C>): Refined<A, C> =
    ComposedRefined({ other.f(f(it)) }, this)

}

@PublishedApi
internal class ComposedRefined<A, B, C>(
  f: (A) -> C,
  val other: Refined<A, B>
) : Refined<A, C>(f, other.constrains)
