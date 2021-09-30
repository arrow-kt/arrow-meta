package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface DeclarationWithInitializer : Declaration {
  val initializer: Expression?
  fun hasInitializer(): Boolean
}
