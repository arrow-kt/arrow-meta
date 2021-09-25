package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinLoopExpression : KotlinExpression {
  val body: KotlinExpression?
}
