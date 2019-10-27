package arrow.meta.dsl.ide.utils

import org.celtric.kotlin.html.BlockElement
import org.celtric.kotlin.html.InlineElement
import org.celtric.kotlin.html.code
import org.celtric.kotlin.html.text

object IdeUtils {
  fun <A> isNotNull(a: A?): Boolean = a?.let { true } ?: false
}

fun <A> kotlin(a: A): InlineElement = code(other = mapOf("lang" to "kotlin")) { "\t$a\n" }
fun kotlin(a: String): InlineElement = code(other = mapOf("lang" to "kotlin")) { "\t${text(a).content}\n" }
fun <A> h1(a: A): BlockElement = org.celtric.kotlin.html.h1("$a")
fun <A> code(a: A): InlineElement = code("\n\t$a\n")
