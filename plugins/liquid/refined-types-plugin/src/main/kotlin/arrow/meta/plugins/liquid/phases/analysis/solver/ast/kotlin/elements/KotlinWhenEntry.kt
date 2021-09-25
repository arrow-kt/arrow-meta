package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinWhenEntry : KotlinElement {
  val isElse: Boolean
  val expression: KotlinExpression?
  val conditions: List<KotlinWhenCondition>
}
