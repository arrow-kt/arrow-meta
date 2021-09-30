package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface TypeConstraint {
  val subjectTypeParameterName: SimpleNameExpression?
  val boundTypeReference: TypeReference?
}
