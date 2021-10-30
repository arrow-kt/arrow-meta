package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface BinaryExpression : OperationExpression {
  val operationToken: String
  val operationTokenRpr: String
  val left: Expression?
  val right: Expression?
}
