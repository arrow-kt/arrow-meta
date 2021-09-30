package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface PureClassOrObject : PureElement, DeclarationContainer {
  val name: String?
  val isLocal: Boolean

  val superTypeListEntries: List<SuperTypeListEntry>

  val companionObjects: List<ObjectDeclaration?>
  fun hasExplicitPrimaryConstructor(): Boolean
  fun hasPrimaryConstructor(): Boolean
  val primaryConstructor: PrimaryConstructor?
  val primaryConstructorModifierList: ModifierList?

  val primaryConstructorParameters: List<Parameter>

  val secondaryConstructors: List<SecondaryConstructor?>
  val body: ClassBody?
}
