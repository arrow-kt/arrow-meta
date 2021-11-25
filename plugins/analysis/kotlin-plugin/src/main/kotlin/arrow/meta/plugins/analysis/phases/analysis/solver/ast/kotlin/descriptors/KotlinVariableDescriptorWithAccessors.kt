package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableAccessorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptorWithAccessors
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

abstract class KotlinVariableDescriptorWithAccessors(
  override val impl: org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
) : VariableDescriptorWithAccessors, KotlinVariableDescriptor(impl) {

  override fun impl(): org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors = impl
  override val getter: VariableAccessorDescriptor?
    get() = impl().getter?.model()
  override val isDelegated: Boolean
    get() = impl().isDelegated
  override val setter: VariableAccessorDescriptor?
    get() = impl().setter?.model()
}
