package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FieldDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyAccessorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

class KotlinPropertyDescriptor(val impl: org.jetbrains.kotlin.descriptors.PropertyDescriptor) :
  PropertyDescriptor, KotlinVariableDescriptorWithAccessors, KotlinCallableMemberDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.PropertyDescriptor = impl
  override val isSetterProjectedOut: Boolean
    get() = impl().isSetterProjectedOut
  override val accessors: List<PropertyAccessorDescriptor>
    get() = impl().accessors.map { it.model() }
  override val backingField: FieldDescriptor?
    get() = impl().backingField?.let { KotlinFieldDescriptor { it } }
  override val delegateField: FieldDescriptor?
    get() = impl().delegateField?.let { KotlinFieldDescriptor { it } }
}
