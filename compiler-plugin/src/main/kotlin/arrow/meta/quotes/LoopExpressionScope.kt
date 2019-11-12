package arrow.meta.quotes

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLoopExpression

/**
 * A base template destructuring [Scope] for all [KtLoopExpression] AST elements
 */
open class LoopExpressionScope<out T : KtLoopExpression>(
  override val value: T,
  val leftParenthesis: PsiElement? = value.leftParenthesis,
  val rightParenthesis: PsiElement? = value.rightParenthesis,
  val body: KtExpression? = value.body
) : Scope<T>(value)