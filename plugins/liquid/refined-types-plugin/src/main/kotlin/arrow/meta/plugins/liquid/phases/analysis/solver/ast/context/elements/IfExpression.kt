package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface IfExpression : Expression {
  val condition: Expression?
  val thenExpression: Expression?
  val elseExpression: Expression?
}
