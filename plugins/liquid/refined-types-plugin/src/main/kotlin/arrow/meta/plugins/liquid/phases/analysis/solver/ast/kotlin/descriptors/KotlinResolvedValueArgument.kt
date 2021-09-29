package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model

open class KotlinResolvedValueArgument(open val impl: org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument) : ResolvedValueArgument {
  open fun impl(): org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument = impl
  override val arguments: List<ValueArgument>
    get() = impl().arguments.map { KotlinValueArgument(it) }
}
