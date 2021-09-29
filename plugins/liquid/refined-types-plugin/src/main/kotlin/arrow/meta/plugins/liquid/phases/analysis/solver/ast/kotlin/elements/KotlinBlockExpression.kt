package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtBlockExpression

class KotlinBlockExpression(val impl: KtBlockExpression) : BlockExpression, KotlinElement, KotlinExpression {
  override fun impl(): KtBlockExpression = impl
  override val firstStatement: Expression?
    get() = impl().firstStatement?.model()
  override val statements: List<Expression>
    get() = impl().statements.map { it.model() }
}
