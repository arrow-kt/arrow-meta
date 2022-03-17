package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface IfExpression : Expression {
  val condition: Expression?
  val thenExpression: Expression?
  val elseExpression: Expression?
}
