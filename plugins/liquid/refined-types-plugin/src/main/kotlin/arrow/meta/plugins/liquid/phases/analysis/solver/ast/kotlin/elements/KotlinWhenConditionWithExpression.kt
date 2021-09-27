package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.WhenConditionWithExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression

class KotlinWhenConditionWithExpression(val impl: KtWhenConditionWithExpression) : WhenConditionWithExpression, KotlinWhenCondition {
  override fun impl(): KtWhenConditionWithExpression = impl
  override val expression: Expression?
    get() = impl().expression?.model()
}
