package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

interface KotlinVariableAccessorDescriptorKotlin : KotlinFunctionDescriptor {
  val correspondingVariable: KotlinVariableDescriptorWithAccessors
}


