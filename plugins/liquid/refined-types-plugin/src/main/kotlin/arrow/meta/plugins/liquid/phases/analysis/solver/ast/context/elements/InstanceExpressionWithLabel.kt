package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface InstanceExpressionWithLabel : ExpressionWithLabel {
  val instanceReference: ReferenceExpression
}
