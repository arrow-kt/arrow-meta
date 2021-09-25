
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinVariableDeclaration : KotlinCallableDeclaration, KotlinDeclarationWithInitializer {
  val isVar: Boolean
}
