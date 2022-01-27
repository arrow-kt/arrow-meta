package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.UnaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtUnaryExpression

class KotlinUnaryExpression(val impl: KtUnaryExpression) :
  UnaryExpression, KotlinExpression, KotlinOperationExpression {
  override fun impl(): KtUnaryExpression = impl
  override val baseExpression: Expression?
    get() = impl().baseExpression?.model()
  override val operationReference: SimpleNameExpression
    get() = impl().operationReference.model()
}
