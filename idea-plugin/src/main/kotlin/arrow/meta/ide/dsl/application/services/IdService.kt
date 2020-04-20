package arrow.meta.ide.dsl.application.services

/**
 * [IdService] is a service wrapper over A [value] and solely allows monomorphic updates.
 * This Service is one way to replace global variables stored in components to their service representation.
 * The [value] A may either be distributed per project or per application.
 */
interface IdService<A> {
  var value: Id<A>

  /**
   * impure [map] that updates the underlying cache
   * with transformation [f].
   */
  fun map(f: (A) -> A): Id<A> =
    value.map(f)
      .also { value = it }
}

/**
 * minimal port from arrow-core
 */
data class Id<A> internal constructor(private val value: A) {
  fun <R> map(f: (A) -> R): Id<R> =
    Id(f(value))

  fun extract(): A = value

  companion object {
    fun <A> just(value: A): Id<A> =
      Id(value)
  }
}