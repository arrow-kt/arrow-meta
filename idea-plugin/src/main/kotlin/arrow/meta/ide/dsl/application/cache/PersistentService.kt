package arrow.meta.ide.dsl.application.cache

import com.intellij.openapi.components.PersistentStateComponent

/**
 * [PersistentService] caches a [value] and solely allows monomorphic updates.
 * This Service is one way to replace global variables stored in components to their service representation,
 * store state in either project-level or application-level, as an effort to migrate to Services.
 */
interface PersistentService<A> : PersistentStateComponent<Id<A>> {
  var value: Id<A>

  override fun getState(): Id<A> =
    value

  override fun loadState(state: Id<A>) {
    value = state
  }

  /**
   * impure [map] that updates the underlying cache
   * with transformation [f].
   */
  fun map(f: (A) -> A): Id<A> =
    value.map(f)
      .also { loadState(it) }
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