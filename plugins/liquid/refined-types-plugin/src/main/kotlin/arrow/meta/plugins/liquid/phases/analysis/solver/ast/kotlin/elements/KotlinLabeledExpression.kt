package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface LabeledExpression : KotlinExpressionWithLabel {
  val baseExpression: KotlinExpression?
}
