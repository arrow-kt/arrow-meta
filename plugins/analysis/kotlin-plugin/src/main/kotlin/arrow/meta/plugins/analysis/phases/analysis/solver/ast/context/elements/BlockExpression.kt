package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface BlockExpression : Element, Expression {
  val firstStatement: Expression?
  val statements: List<Expression>
}
