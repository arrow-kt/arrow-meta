package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

interface KotlinPostfixExpression : KotlinUnaryExpression {
  override val baseExpression: KotlinExpression?
}
