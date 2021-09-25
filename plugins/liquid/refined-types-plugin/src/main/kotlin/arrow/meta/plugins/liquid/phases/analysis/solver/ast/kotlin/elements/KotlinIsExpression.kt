package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.IsExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtIsExpression

fun interface KotlinIsExpression : IsExpression, KotlinExpression, KotlinOperationExpression {
  override fun impl(): KtIsExpression
  override val leftHandSide: Expression
    get() = impl().leftHandSide.model()
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
  override val operationReference: SimpleNameExpression
    get() = impl().operationReference.model()
  override val isNegated: Boolean
    get() = impl().isNegated
}
