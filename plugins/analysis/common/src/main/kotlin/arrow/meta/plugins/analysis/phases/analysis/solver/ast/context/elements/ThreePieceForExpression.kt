package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ThreePieceForExpression : LoopExpression {
  val initializer: List<Expression>
  val condition: Expression?
  val update: List<Expression>
}
