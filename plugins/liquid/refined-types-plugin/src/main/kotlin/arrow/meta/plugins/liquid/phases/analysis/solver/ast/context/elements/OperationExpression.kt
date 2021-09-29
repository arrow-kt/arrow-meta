package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface OperationExpression : Expression {
  val operationReference: SimpleNameExpression
}
