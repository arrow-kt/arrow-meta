package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.OperationReferenceExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.WhenConditionInRange
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtWhenConditionInRange

fun interface KotlinWhenConditionInRange : WhenConditionInRange, KotlinWhenCondition {
  override fun impl(): KtWhenConditionInRange
  override val isNegated: Boolean
    get() = impl().isNegated
  override val rangeExpression: Expression?
    get() = impl().rangeExpression?.model()
  override val operationReference: OperationReferenceExpression
    get() = impl().operationReference.model()
}
