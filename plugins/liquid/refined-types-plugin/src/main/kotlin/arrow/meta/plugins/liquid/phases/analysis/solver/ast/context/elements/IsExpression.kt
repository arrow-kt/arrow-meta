package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface IsExpression : Expression, OperationExpression {
  val leftHandSide: Expression
  val typeReference: TypeReference?
  override val operationReference: SimpleNameExpression
  val isNegated: Boolean
}
