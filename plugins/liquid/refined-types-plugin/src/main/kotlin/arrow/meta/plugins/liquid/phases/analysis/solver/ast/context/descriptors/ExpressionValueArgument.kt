package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgument

interface ExpressionValueArgument : ResolvedValueArgument {
  val valueArgument: ValueArgument?
}
