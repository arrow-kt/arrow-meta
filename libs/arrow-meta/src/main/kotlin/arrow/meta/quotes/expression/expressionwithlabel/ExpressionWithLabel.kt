package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/** A template destructuring [Scope] for a [KtExpressionWithLabel] */
open class ExpressionWithLabel<out T : KtExpressionWithLabel>(
  override val value: T,
  open val targetLabel: Scope<KtSimpleNameExpression> = Scope(value.getTargetLabel()),
  open val labelName: String? = value.getLabelName()
) : Scope<T>(value)
