package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.IfExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtIfExpression

class KotlinIfExpression(val impl: KtIfExpression) : IfExpression, KotlinExpression {
  override fun impl(): KtIfExpression = impl
  override val condition: Expression?
    get() = impl().condition?.model()
  override val thenExpression: Expression?
    get() = impl().then?.model()
  override val elseExpression: Expression?
    get() = impl().`else`?.model()
}
