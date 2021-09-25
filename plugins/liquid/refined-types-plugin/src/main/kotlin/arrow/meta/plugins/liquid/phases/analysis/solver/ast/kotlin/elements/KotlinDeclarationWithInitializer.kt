package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinDeclarationWithInitializer : KotlinDeclaration {
  val initializer: KotlinExpression?
  fun hasInitializer(): Boolean
}
