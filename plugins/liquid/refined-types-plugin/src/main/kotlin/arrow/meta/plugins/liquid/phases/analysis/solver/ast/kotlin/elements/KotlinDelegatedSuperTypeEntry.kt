package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinDelegatedSuperTypeEntry : KotlinSuperTypeListEntry {
  val delegateExpression: KotlinExpression?
}
