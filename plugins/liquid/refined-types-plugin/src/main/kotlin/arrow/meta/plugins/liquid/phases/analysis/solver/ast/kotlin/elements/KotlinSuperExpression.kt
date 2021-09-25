package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinSuperExpression : KotlinInstanceExpressionWithLabel {
  val superTypeQualifier: KotlinTypeReference?
}
