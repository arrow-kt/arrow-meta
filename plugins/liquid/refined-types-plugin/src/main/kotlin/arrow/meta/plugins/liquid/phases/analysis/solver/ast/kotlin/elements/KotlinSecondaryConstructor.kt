package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinSecondaryConstructor : KotlinConstructor<KotlinSecondaryConstructor> {
  fun getDelegationCall(): KotlinConstructorDelegationCall?
}
