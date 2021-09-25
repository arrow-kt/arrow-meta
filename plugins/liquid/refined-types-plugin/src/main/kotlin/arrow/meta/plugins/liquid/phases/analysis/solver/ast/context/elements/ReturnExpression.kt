package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ReturnExpression : ExpressionWithLabel {
  val returnedExpression: Expression?
  val labeledExpression: Expression?
}
