package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface UnaryExpression : Expression, OperationExpression {
  val baseExpression: Expression?
  override val operationReference: SimpleNameExpression
}
