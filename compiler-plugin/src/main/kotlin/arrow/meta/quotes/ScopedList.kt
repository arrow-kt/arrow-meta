package arrow.meta.quotes

import org.jetbrains.kotlin.psi.KtElement

data class ScopedList<K : KtElement>(
  val value: List<K>,
  val prefix: String = "",
  val separator: String = ", ",
  val postfix: String = "",
  val forceRenderSurroundings: Boolean = false,
  val transform: (K) -> String = { it.text }
) {

  override fun toString(): String =
    if (value.isEmpty())
      if (forceRenderSurroundings) prefix + postfix
      else ""
    else value.filterNot { it.text == "null" }.joinToString( //some java values
      separator = separator,
      prefix = prefix,
      postfix = postfix,
      transform = transform
    )

  fun toStringList(): List<String> {
    val list = arrayListOf<String>()
    if (value.isEmpty()) {
      if (forceRenderSurroundings) list.add(prefix + postfix)
    } else {
      list.addAll(value.mapNotNull {it.text})
    }
    return list
  }

  fun isEmpty() = value.isEmpty()

  companion object {
    fun <K: KtElement> empty(): ScopedList<K> = ScopedList(emptyList())
  }
}

fun <K : KtElement> ScopedList<K>.map(f: (K) -> K?): ScopedList<K> =
  copy(value.mapNotNull(f))

fun <A, K : KtElement> ScopedList<K>.fold(a: A, f: (A, K) -> A): A =
  value.fold(a, f)

fun <A, K : KtElement> ScopedList<K>.foldIndexed(a: A, f: (Int, A, K) -> A): A =
  value.foldIndexed(a, f)

operator fun <K : KtElement> ScopedList<K>.plus(k: ScopedList<K>): ScopedList<K> =
  copy(value + k.value)

operator fun <K : KtElement> ScopedList<K>.plus(k: Scope<K>): ScopedList<K> =
  k.value?.let { copy((value + k.value).filterNotNull()) } ?: this