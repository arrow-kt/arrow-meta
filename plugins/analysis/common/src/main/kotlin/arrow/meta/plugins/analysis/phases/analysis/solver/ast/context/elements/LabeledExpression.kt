package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface LabeledExpression : ExpressionWithLabel {
  val baseExpression: Expression?
}
