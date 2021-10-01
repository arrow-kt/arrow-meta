package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ParenthesizedExpression : Expression {
  val expression: Expression?
}
