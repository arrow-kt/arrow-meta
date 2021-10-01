package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface OperationReferenceExpression : SimpleNameExpression {
  fun isConventionOperator(): Boolean
}
