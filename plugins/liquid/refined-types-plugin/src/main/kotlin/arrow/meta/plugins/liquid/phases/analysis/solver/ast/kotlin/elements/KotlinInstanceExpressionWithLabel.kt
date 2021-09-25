package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinInstanceExpressionWithLabel : KotlinExpressionWithLabel {
  val instanceReference: KotlinReferenceExpression
}
