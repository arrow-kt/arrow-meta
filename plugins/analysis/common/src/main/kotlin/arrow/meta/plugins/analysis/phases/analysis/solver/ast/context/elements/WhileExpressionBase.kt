package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface WhileExpressionBase : LoopExpression {
  val condition: Expression?
}
