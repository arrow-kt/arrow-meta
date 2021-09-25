package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinTypeElement : KotlinElement {
  val typeArgumentsAsTypes: List<KotlinTypeReference>
}
