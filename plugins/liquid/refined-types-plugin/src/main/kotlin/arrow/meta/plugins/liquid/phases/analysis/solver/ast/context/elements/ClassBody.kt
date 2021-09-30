package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ClassBody : DeclarationContainer, Element {
  val anonymousInitializers: List<AnonymousInitializer>
  val properties: List<Property>
  val functions: List<NamedFunction>
  val enumEntries: List<EnumEntry>
  val allCompanionObjects: List<ObjectDeclaration>
}
