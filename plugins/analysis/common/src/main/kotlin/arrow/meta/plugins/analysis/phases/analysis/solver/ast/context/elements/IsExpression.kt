package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface IsExpression : Expression, OperationExpression {
  val leftHandSide: Expression
  val typeReference: TypeReference?
  override val operationReference: SimpleNameExpression
  val isNegated: Boolean
}
