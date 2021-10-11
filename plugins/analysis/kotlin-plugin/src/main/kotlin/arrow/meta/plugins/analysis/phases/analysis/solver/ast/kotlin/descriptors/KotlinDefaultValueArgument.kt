package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DefaultValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentName

open class KotlinDefaultValueArgument(
  override val impl: org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
) : ValueArgument, DefaultValueArgument, KotlinResolvedValueArgument(impl) {
  override fun impl(): org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument = impl

  override val argumentExpression: Expression?
    get() = valueArgument?.argumentExpression

  override val isSpread: Boolean
    get() = valueArgument?.isSpread ?: false

  override fun getArgumentName(): ValueArgumentName? =
    valueArgument?.getArgumentName()

  override fun isNamed(): Boolean =
    valueArgument?.isNamed() == true

  override fun isExternal(): Boolean =
    valueArgument?.isExternal() == true

  override val valueArgument: ValueArgument?
    get() = impl().arguments.getOrNull(0)?.let { KotlinValueArgument(it) }
}
