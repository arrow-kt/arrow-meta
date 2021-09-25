package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.FieldDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.PropertyAccessorDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model


fun interface KotlinPropertyDescriptor : PropertyDescriptor, KotlinVariableDescriptorWithAccessors, KotlinCallableMemberDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.PropertyDescriptor
  override val isSetterProjectedOut: Boolean
    get() = impl().isSetterProjectedOut
  override val accessors: List<PropertyAccessorDescriptor>
    get() = impl().accessors.map { it.model() }
  override val backingField: FieldDescriptor?
    get() = impl().backingField?.let { KotlinFieldDescriptor { it } }
  override val delegateField: FieldDescriptor?
    get() = impl().delegateField?.let { KotlinFieldDescriptor { it } }
}
