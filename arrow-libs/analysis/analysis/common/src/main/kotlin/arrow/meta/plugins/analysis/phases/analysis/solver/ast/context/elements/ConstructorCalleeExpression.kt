package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ConstructorCalleeExpression {
  val typeReference: TypeReference?
  val constructorReferenceExpression: SimpleNameExpression?
}
