package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinSelfType : KotlinTypeElement {
  override val typeArgumentsAsTypes: List<KotlinTypeReference>
}
