package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface CallableReferenceExpression : DoubleColonExpression {
  val callableReference: SimpleNameExpression
}
