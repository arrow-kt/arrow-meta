package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallElement
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionLambdaArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeProjection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinValueArgument
import org.jetbrains.kotlin.psi.KtCallElement

fun interface KotlinCallElement : CallElement, KotlinElement {
  override fun impl(): KtCallElement
  override val calleeExpression: Expression?
    get() = impl().calleeExpression?.model()
  override val valueArguments: List<ValueArgument>
    get() = impl().valueArguments.map { KotlinValueArgument(it) }
  override val lambdaArguments: List<ExpressionLambdaArgument>
    get() = impl().lambdaArguments.map { it.model() }
  override val typeArguments: List<TypeProjection>
    get() = TODO("Not yet implemented")
}
