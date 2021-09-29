package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface DestructuringDeclaration : DeclarationWithInitializer {
  val entries: List<DestructuringDeclarationEntry>
  val isVar: Boolean
}
