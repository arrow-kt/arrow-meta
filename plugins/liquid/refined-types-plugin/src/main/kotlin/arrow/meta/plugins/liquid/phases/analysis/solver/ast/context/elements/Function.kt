package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Function : DeclarationWithBody, CallableDeclaration {
  val isLocal: Boolean
}
