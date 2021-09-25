package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface WhenConditionIsPattern : WhenCondition {
  val isNegated: Boolean
  val typeReference: TypeReference?
}
