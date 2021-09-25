package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinObjectDeclaration : KotlinClassOrObject {
  fun isCompanion(): Boolean
  fun isObjectLiteral(): Boolean
}
