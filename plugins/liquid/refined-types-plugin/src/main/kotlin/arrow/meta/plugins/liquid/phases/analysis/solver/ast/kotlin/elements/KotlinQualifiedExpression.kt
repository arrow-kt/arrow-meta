package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinQualifiedExpression : KotlinExpression {
  val receiverExpression: KotlinExpression
  val selectorExpression: KotlinExpression?
}
