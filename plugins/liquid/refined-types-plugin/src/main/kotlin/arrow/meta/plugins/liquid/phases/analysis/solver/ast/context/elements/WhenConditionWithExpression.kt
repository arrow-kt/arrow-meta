package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface WhenConditionWithExpression : WhenCondition {
  val expression: Expression?
}
