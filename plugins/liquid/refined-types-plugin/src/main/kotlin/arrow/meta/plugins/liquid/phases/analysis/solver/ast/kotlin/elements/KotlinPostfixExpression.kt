
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinPostfixExpression : KotlinUnaryExpression {
  override val baseExpression: KotlinExpression?
}
