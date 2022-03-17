package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface Property : VariableDeclaration {
  val isLocal: Boolean
  val isMember: Boolean
  val isTopLevel: Boolean
  val accessors: List<PropertyAccessor?>
  val getter: PropertyAccessor?
  val setter: PropertyAccessor?
  fun hasDelegate(): Boolean
  val delegate: PropertyDelegate?
  fun hasDelegateExpression(): Boolean
  val delegateExpression: Expression?
  fun hasDelegateExpressionOrInitializer(): Boolean
  val delegateExpressionOrInitializer: Expression?
  fun hasBody(): Boolean
}
