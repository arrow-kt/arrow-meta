package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinFinallySection : KotlinElement {
  val finalExpression: KotlinBlockExpression
}
