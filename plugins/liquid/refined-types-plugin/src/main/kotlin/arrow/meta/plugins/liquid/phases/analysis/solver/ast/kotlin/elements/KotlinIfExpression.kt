package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinIfExpression : KotlinExpression {
  val condition: KotlinExpression?
  val thenExpression: KotlinExpression?
  val elseExpression: KotlinExpression?
}
