package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinWhenConditionInRange : KotlinWhenCondition {
  val isNegated: Boolean
  val rangeExpression: KotlinExpression?
  val operationReference: KotlinOperationReferenceExpression
}
