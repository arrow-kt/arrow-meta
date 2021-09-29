package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ArrayAccessExpression : ReferenceExpression {
  val arrayExpression: Expression?
  val indexExpressions: List<Expression>
}
