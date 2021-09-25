package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface SuperExpression : InstanceExpressionWithLabel {
  val superTypeQualifier: TypeReference?
}
