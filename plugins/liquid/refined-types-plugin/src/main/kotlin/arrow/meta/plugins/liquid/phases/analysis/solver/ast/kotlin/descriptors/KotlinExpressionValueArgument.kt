package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgument

interface KotlinExpressionValueArgument : KotlinResolvedValueArgument {
  val valueArgument: ValueArgument
}
