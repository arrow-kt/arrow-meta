package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument

interface ExpressionValueArgument : ResolvedValueArgument {
  val valueArgument: ValueArgument?
}
