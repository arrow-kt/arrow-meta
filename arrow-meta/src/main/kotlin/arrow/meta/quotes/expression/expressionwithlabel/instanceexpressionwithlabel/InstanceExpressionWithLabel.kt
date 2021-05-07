package arrow.meta.quotes.expression.expressionwithlabel.instanceexpressionwithlabel

import arrow.meta.quotes.Scope
import arrow.meta.quotes.expression.expressionwithlabel.ExpressionWithLabel
import org.jetbrains.kotlin.psi.KtInstanceExpressionWithLabel
import org.jetbrains.kotlin.psi.KtReferenceExpression

open class InstanceExpressionWithLabel<out T: KtInstanceExpressionWithLabel>(
  override val value: T,
  val instanceReference: Scope<KtReferenceExpression> = Scope(value.instanceReference)
) : ExpressionWithLabel<T>(value)