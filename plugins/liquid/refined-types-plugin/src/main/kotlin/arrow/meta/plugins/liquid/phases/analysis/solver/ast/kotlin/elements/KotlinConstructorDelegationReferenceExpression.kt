package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinConstructorDelegationReferenceExpression : KotlinExpression, KotlinReferenceExpression {
  val isThis: Boolean
}
