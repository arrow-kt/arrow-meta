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
}
