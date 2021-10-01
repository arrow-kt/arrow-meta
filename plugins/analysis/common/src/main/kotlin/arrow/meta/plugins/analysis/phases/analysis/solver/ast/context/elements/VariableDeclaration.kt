
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface VariableDeclaration : CallableDeclaration, DeclarationWithInitializer {
  val isVar: Boolean
}
