package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.InstanceExpressionWithLabel
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ReferenceExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtInstanceExpressionWithLabel

fun interface KotlinInstanceExpressionWithLabel : InstanceExpressionWithLabel,  KotlinExpressionWithLabel {
  override fun impl(): KtInstanceExpressionWithLabel
  override val instanceReference: ReferenceExpression
    get() = impl().instanceReference.model()
}
