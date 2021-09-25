package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinConstructorDelegationCall : KotlinElement, KotlinCallElement {
  val isImplicit: Boolean
  val isCallToThis: Boolean
}
