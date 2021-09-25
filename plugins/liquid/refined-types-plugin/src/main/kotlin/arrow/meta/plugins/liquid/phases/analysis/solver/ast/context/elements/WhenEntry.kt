package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface WhenEntry : Element {
  val isElse: Boolean
  val expression: Expression?
  val conditions: List<WhenCondition>
}
