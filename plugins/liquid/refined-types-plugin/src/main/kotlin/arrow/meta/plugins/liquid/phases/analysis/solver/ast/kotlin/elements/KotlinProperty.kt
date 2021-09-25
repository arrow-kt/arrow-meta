package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinProperty : KotlinVariableDeclaration {
  val isLocal: Boolean
  val isMember: Boolean
  val isTopLevel: Boolean
  val accessors: List<KotlinPropertyAccessor?>
  val getter: KotlinPropertyAccessor?
  val setter: KotlinPropertyAccessor?
  fun hasDelegate(): Boolean
  val delegate: KotlinPropertyDelegate?
  fun hasDelegateExpression(): Boolean
  val delegateExpression: KotlinExpression?
  fun hasDelegateExpressionOrInitializer(): Boolean
  val delegateExpressionOrInitializer: KotlinExpression?
  fun hasBody(): Boolean
}
