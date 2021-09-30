package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface OperationReferenceExpression : SimpleNameExpression {
  fun isConventionOperator(): Boolean
}
