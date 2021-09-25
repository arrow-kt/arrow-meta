package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ExpressionValueArgument : Element {
  val argumentExpression: Expression?
  val argumentName: ExpressionValueArgumentName?
  val isSpread: Boolean
}
