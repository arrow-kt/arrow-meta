package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ForExpression : LoopExpression {
  val loopParameter: Parameter?
  val destructuringDeclaration: DestructuringDeclaration?
  val loopRange: Expression?
}
