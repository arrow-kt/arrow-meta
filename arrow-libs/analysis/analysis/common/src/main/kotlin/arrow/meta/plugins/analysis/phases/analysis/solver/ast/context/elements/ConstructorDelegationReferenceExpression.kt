package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ConstructorDelegationReferenceExpression : Expression, ReferenceExpression {
  val isThis: Boolean
}
