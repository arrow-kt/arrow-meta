package arrow.meta.phases.analysis

import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.astReplace

fun KtFunction.body(): KtExpression? =
  bodyExpression ?: bodyBlockExpression

fun KtExpression.bodySourceAsExpression(): String? =
  when (this) {
    is KtBlockExpression -> statements.map {
      when (it) {
        is KtReturnExpression -> it.returnedExpression?.text
        else -> text
      }
    }.joinToString("\n").drop(1).dropLast(1)
    else -> text
  }

fun KtElement.transform(f: (KtElement) -> KtElement?): KtElement {
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      val result = f(element)
      if (result != null)  {
        element.astReplace(result)
      }
      super.visitKtElement(element)
    }
  })
  return this
}

fun KtElement.dfs(f: (KtElement) -> Boolean): List<KtElement> {
  val found = arrayListOf<KtElement>()
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      val result = f(element)
      if (result) found.add(element)
      super.visitKtElement(element)
    }
  })
  return found
}

fun KtAnnotated.isAnnotatedWith(regex: Regex) =
  annotationEntries.any { it.text.matches(regex) }

