package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ContinueExpression
import org.jetbrains.kotlin.psi.KtContinueExpression

class KotlinContinueExpression(val impl: KtContinueExpression) :
  ContinueExpression, KotlinExpressionWithLabel {
  override fun impl(): KtContinueExpression = impl
}
