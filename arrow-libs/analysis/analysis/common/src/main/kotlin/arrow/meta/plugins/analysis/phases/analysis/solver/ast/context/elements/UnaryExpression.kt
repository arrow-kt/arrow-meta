package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface UnaryExpression : Expression, OperationExpression {
  val baseExpression: Expression?
  override val operationReference: SimpleNameExpression
}
