package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface Function : DeclarationWithBody, CallableDeclaration {
  val isLocal: Boolean
}
