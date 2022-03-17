package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface DeclarationWithInitializer : Declaration {
  val initializer: Expression?
  fun hasInitializer(): Boolean
}
