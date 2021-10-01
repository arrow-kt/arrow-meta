package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface SuperExpression : InstanceExpressionWithLabel {
  val superTypeQualifier: TypeReference?
}
