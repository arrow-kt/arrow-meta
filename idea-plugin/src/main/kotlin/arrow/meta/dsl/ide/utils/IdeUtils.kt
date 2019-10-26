package arrow.meta.dsl.ide.utils

object IdeUtils {
  fun <A> isNotNull(a: A?): Boolean = a?.let { true } ?: false
}

/*
fun String.toCode(): InlineElement =
  code(other = mapOf("lang" to "kotlin"), content = { this })*/
