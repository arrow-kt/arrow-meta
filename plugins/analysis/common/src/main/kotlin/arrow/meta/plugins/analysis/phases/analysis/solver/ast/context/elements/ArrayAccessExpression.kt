package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ArrayAccessExpression : ReferenceExpression {
  val arrayExpression: Expression?
  val indexExpressions: List<Expression>
}
