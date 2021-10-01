package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument

open class KotlinResolvedValueArgument(open val impl: org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument) : ResolvedValueArgument {
  open fun impl(): org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument = impl
  override val arguments: List<ValueArgument>
    get() = impl().arguments.map { KotlinValueArgument(it) }
}
