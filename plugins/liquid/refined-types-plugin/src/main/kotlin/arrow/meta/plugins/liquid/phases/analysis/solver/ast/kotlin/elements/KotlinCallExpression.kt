package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.CallExpression
import org.jetbrains.kotlin.psi.KtCallExpression

class KotlinCallExpression(val impl: KtCallExpression) : CallExpression, KotlinCallElement, KotlinReferenceExpression {
  override fun impl(): KtCallExpression = impl
}

