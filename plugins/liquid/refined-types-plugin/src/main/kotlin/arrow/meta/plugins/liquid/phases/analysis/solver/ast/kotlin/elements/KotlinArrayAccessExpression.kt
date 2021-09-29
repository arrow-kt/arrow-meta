package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ArrayAccessExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtArrayAccessExpression

class KotlinArrayAccessExpression(
  val impl: KtArrayAccessExpression
) : ArrayAccessExpression, KotlinReferenceExpression {
  override fun impl(): KtArrayAccessExpression = impl
  override val arrayExpression: Expression?
    get() = impl().arrayExpression?.model()
  override val indexExpressions: List<Expression>
    get() = impl().indexExpressions.map { it.model() }
}
