package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model

fun interface KotlinResolvedValueArgument: ResolvedValueArgument {
  fun impl(): org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
  override val arguments: List<ValueArgument>
    get() = impl().arguments.map { it.asElement().model() }
}
