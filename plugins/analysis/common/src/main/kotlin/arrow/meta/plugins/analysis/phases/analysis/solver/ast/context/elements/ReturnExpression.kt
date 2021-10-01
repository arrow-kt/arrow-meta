package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ReturnExpression : ExpressionWithLabel {
  val returnedExpression: Expression?
  val labeledExpression: Expression?
}
