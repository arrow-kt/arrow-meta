package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinForExpression : KotlinLoopExpression {
  val loopParameter: KotlinParameter?
  val destructuringDeclaration: KotlinDestructuringDeclaration?
  val loopRange: KotlinExpression?
}
