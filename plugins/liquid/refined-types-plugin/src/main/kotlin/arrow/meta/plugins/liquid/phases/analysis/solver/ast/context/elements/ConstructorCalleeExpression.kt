package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ConstructorCalleeExpression {
  val typeReference: TypeReference?
  val constructorReferenceExpression: SimpleNameExpression?
}
