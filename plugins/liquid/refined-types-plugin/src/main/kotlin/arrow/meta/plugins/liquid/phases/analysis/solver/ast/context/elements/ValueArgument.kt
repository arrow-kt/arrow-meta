package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ValueArgument : ExpressionResolvedValueArgument {
  fun getArgumentName(): ValueArgumentName?
  fun isNamed(): Boolean
  fun isExternal(): Boolean
}
