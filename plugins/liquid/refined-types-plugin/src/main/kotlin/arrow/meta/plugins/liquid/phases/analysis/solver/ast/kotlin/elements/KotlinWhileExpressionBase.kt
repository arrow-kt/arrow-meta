package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinWhileExpressionBase : KotlinLoopExpression {
  val condition: KotlinExpression?
}
