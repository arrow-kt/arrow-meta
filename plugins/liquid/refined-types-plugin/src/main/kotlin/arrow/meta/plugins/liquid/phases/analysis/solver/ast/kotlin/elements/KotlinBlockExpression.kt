package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtBlockExpression

fun interface KotlinBlockExpression : BlockExpression, KotlinElement, KotlinExpression {
  override fun impl(): KtBlockExpression
  override val firstStatement: Expression?
    get() = impl().firstStatement?.model()
  override val statements: List<Expression>
    get() = impl().statements.map { it.model() }
}
