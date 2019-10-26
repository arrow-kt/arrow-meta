package arrow.meta.dsl.ide.utils

object IdeUtils {
  fun <A> isNotNull(a: A?): Boolean = a?.let { true } ?: false
}

fun <T> Array<T>.firstOrDefault(default: T): T =
  firstOrNull() ?: default