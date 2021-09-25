package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ConstructorDelegationReferenceExpression : Expression, ReferenceExpression {
  val isThis: Boolean
}
