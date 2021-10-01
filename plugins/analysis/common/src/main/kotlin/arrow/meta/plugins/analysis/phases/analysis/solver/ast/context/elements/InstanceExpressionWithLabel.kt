package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface InstanceExpressionWithLabel : ExpressionWithLabel {
  val instanceReference: ReferenceExpression
}
