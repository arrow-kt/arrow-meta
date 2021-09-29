package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.UnaryExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtUnaryExpression

fun interface KotlinUnaryExpression : UnaryExpression, KotlinExpression, KotlinOperationExpression {
  override fun impl(): KtUnaryExpression
  override val baseExpression: Expression?
    get() = impl().baseExpression?.model()
  override val operationReference: SimpleNameExpression
    get() = impl().operationReference.model()
}
