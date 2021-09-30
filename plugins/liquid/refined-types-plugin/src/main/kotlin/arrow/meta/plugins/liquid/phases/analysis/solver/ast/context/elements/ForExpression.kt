package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ForExpression : LoopExpression {
  val loopParameter: Parameter?
  val destructuringDeclaration: DestructuringDeclaration?
  val loopRange: Expression?
}
