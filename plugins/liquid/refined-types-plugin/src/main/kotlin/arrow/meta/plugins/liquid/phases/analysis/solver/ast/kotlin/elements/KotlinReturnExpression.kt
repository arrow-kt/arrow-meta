package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinReturnExpression : KotlinExpressionWithLabel {
  val returnedExpression: KotlinExpression?
  val labeledExpression: KotlinExpression?
}
