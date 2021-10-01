package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface WhenConditionInRange : WhenCondition {
  val isNegated: Boolean
  val rangeExpression: Expression?
  val operationReference: OperationReferenceExpression
}
