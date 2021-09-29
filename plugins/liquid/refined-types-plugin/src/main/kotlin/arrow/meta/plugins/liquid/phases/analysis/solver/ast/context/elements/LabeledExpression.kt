package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface LabeledExpression : ExpressionWithLabel {
  val baseExpression: Expression?
}
