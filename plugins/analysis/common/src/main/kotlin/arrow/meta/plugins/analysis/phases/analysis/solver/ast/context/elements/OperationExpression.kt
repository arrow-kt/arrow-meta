package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface OperationExpression : Expression {
  val operationReference: SimpleNameExpression
}
