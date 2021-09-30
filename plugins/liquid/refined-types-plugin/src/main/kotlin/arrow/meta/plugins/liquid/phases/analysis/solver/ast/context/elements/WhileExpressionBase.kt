package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface WhileExpressionBase : LoopExpression {
  val condition: Expression?
}
