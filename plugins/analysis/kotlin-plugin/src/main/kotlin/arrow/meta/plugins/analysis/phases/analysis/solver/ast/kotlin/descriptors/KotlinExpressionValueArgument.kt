package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ExpressionValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements.KotlinValueArgumentName

open class KotlinExpressionValueArgument(
  override val impl: org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
) : ValueArgument, ExpressionValueArgument, ExpressionResolvedValueArgument, KotlinResolvedValueArgument(impl) {
  override fun impl(): org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument = impl
  override val argumentExpression: Expression?
    get() = impl().valueArgument?.getArgumentExpression()?.model()
  override val isSpread: Boolean
    get() = impl().valueArgument?.getSpreadElement() != null

  override fun getArgumentName(): ValueArgumentName? =
    impl().valueArgument?.getArgumentName()?.let { KotlinValueArgumentName(it) }

  override fun isNamed(): Boolean =
    impl().valueArgument?.isNamed() == true

  override fun isExternal(): Boolean =
    impl().valueArgument?.isExternal() == true

  override val valueArgument: ValueArgument?
    get() = impl().valueArgument?.let { KotlinValueArgument(it) }
}
