package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface WhenConditionIsPattern : WhenCondition {
  val isNegated: Boolean
  val typeReference: TypeReference?
}
