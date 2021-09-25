package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinParameter : KotlinCallableDeclaration {
  fun hasDefaultValue(): Boolean
  val defaultValue: KotlinExpression?
  val isMutable: Boolean
  val isVarArg: Boolean
  fun hasValOrVar(): Boolean
  val destructuringDeclaration: KotlinDestructuringDeclaration?
  val isLoopParameter: Boolean
  val isCatchParameter: Boolean
  val ownerFunction: KotlinDeclarationWithBody?
}
