package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface DoubleColonExpression : Expression {
  val receiverExpression: Expression?
  val hasQuestionMarks: Boolean
  val isEmptyLHS: Boolean
}
