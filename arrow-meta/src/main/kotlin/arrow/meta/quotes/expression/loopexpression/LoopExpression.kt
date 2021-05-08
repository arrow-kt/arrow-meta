package arrow.meta.quotes.expression.loopexpression

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLoopExpression

/**
 * A base template destructuring [Scope] for all [KtLoopExpression] AST elements
 */
open class LoopExpression<out T : KtLoopExpression>(
  override val value: T,
  val body: Scope<KtExpression> = Scope(value.body)
) : Scope<T>(value)