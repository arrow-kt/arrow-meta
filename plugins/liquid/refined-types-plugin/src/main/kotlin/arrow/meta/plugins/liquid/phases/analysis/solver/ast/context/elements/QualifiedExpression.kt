package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface QualifiedExpression : Expression {
  val receiverExpression: Expression
  val selectorExpression: Expression?
}
