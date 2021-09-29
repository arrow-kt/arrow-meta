
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface VariableDeclaration : CallableDeclaration, DeclarationWithInitializer {
  val isVar: Boolean
}
