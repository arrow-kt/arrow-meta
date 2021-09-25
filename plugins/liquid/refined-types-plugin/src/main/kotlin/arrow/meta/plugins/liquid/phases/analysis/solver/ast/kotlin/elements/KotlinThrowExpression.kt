package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinThrowExpression : KotlinExpression {
  val thrownExpression: KotlinExpression?
}
