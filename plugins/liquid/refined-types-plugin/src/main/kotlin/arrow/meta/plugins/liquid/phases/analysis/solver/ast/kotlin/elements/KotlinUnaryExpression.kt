package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinUnaryExpression : KotlinExpression, KotlinOperationExpression {
  val baseExpression: KotlinExpression?
  override val operationReference: KotlinSimpleNameExpression
}
