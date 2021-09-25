package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinWhenConditionIsPattern : KotlinWhenCondition {
  val isNegated: Boolean
  val typeReference: KotlinTypeReference?
}
