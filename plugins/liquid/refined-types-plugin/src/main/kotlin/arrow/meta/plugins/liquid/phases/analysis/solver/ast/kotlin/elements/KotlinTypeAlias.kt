package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinTypeAlias : KotlinNamedDeclaration {
  fun isTopLevel(): Boolean
  fun getTypeReference(): KotlinTypeReference?
}
