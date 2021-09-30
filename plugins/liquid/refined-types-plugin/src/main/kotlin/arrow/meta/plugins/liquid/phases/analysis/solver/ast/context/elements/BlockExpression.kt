package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface BlockExpression : Element, Expression {
  val firstStatement: Expression?
  val statements: List<Expression>
}
