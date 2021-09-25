package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinWhenConditionWithExpression : KotlinWhenCondition {
  val expression: KotlinExpression?
}
