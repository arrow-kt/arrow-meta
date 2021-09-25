package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

interface KotlinPropertyAccessorDescriptorKotlin : KotlinVariableAccessorDescriptorKotlin {
  override val overriddenDescriptors: Collection<KotlinPropertyAccessorDescriptorKotlin>
}
