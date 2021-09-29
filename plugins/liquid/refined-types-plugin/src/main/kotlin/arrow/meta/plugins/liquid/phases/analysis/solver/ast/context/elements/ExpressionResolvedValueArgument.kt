package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ExpressionResolvedValueArgument {
  val argumentExpression: Expression?
  val isSpread: Boolean
}
