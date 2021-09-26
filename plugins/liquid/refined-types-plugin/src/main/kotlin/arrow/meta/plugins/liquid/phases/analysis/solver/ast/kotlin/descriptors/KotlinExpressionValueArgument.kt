package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ExpressionValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgument

interface KotlinExpressionValueArgument : ExpressionValueArgument, KotlinResolvedValueArgument {
  val valueArgument: ValueArgument
}
