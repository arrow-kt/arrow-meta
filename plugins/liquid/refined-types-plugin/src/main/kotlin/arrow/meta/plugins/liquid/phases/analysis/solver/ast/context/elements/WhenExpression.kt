package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface WhenExpression : Expression {
  val entries: List<WhenEntry>
  val subjectVariable: Property?
  val subjectExpression: Expression?
  val elseExpression: Expression?
}
