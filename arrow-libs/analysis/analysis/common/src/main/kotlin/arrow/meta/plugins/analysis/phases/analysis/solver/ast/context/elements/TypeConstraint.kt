package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeConstraint {
  val subjectTypeParameterName: SimpleNameExpression?
  val boundTypeReference: TypeReference?
}
