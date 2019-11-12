package arrow.meta.ide.dsl.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import org.celtric.kotlin.html.BlockElement
import org.celtric.kotlin.html.InlineElement
import org.celtric.kotlin.html.code
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.psi.KtElement

object IdeUtils {
  fun <A> isNotNull(a: A?): Boolean = a?.let { true } ?: false
}

fun <A : PsiElement, B> PsiElement.traverseFilter(on: Class<A>, f: (A) -> B): List<B> =
  SyntaxTraverser.psiTraverser(this).filter(on).map(f).toList()

fun <A> List<A?>.toNotNullable(): List<A> = fold(emptyList()) { acc: List<A>, r: A? -> if (r != null) acc + r else acc }

fun <A> kotlin(a: A): InlineElement = code(other = mapOf("lang" to "kotlin")) { "\t$a\n" }
fun kotlin(a: String): InlineElement = code(other = mapOf("lang" to "kotlin")) { "\t${text(a).content}\n" }
fun <A> h1(a: A): BlockElement = org.celtric.kotlin.html.h1("$a")
fun <A> code(a: A): InlineElement = code("\n\t$a\n")
