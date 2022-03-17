package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface WhenConditionWithExpression : WhenCondition {
  val expression: Expression?
}
