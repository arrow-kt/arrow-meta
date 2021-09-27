package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ThisExpression
import org.jetbrains.kotlin.psi.KtThisExpression

class KotlinThisExpression(val impl: KtThisExpression) : ThisExpression, KotlinInstanceExpressionWithLabel {
  override fun impl(): KtThisExpression = impl
}
