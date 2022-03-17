package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ExpressionResolvedValueArgument {
  val argumentExpression: Expression?
  val isSpread: Boolean
}
