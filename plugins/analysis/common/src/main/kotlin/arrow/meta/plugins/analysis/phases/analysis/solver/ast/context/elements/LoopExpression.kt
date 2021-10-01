package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface LoopExpression : Expression {
  val body: Expression?
}
