package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinParenthesizedExpression : KotlinExpression {
  val expression: KotlinExpression?
}
