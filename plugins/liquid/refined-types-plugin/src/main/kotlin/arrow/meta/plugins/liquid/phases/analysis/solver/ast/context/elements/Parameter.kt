package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Parameter : CallableDeclaration {
  fun hasDefaultValue(): Boolean
  val defaultValue: Expression?
  val isMutable: Boolean
  val isVarArg: Boolean
  fun hasValOrVar(): Boolean
  val destructuringDeclaration: DestructuringDeclaration?
  val isLoopParameter: Boolean
  val isCatchParameter: Boolean
  val ownerFunction: DeclarationWithBody?
}
