package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface QualifiedExpression : Expression {
  val receiverExpression: Expression
  val selectorExpression: Expression?
}
