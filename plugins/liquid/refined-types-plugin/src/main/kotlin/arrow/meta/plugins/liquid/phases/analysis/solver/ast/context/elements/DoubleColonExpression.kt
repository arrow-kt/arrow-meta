package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface DoubleColonExpression : Expression {
  val receiverExpression: Expression?
  val hasQuestionMarks: Boolean
  val isEmptyLHS: Boolean
}
