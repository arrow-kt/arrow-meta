package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyAccessorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptorWithAccessors
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

class KotlinPropertyAccessorDescriptor(override val impl: org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor) : PropertyAccessorDescriptor, KotlinVariableAccessorDescriptor(impl) {
  override fun impl(): org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor = impl
  override val overriddenDescriptors: Collection<PropertyAccessorDescriptor>
    get() = impl().overriddenDescriptors.map { it.model() }
  override val correspondingVariable: VariableDescriptorWithAccessors
    get() = impl().correspondingVariable.model()
}
