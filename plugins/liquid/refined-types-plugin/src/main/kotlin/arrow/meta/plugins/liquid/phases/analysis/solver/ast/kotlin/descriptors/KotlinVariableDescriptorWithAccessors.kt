package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.VariableAccessorDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.VariableDescriptorWithAccessors
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model

fun interface KotlinVariableDescriptorWithAccessors :
  VariableDescriptorWithAccessors,
  KotlinVariableDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
  override val getter: VariableAccessorDescriptor?
    get() = impl().getter?.model()
  override val isDelegated: Boolean
    get() = impl().isDelegated
  override val setter: VariableAccessorDescriptor?
    get() = impl().setter?.model()
}
