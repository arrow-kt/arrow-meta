package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface WhenEntry : Element {
  val isElse: Boolean
  val expression: Expression?
  val conditions: List<WhenCondition>
}
