package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableAccessorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptorWithAccessors
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

abstract class KotlinVariableAccessorDescriptor(
  override val impl: org.jetbrains.kotlin.descriptors.VariableAccessorDescriptor
) : VariableAccessorDescriptor, KotlinFunctionDescriptor(impl) {
  override fun impl(): org.jetbrains.kotlin.descriptors.VariableAccessorDescriptor = impl
  override val correspondingVariable: VariableDescriptorWithAccessors
    get() = impl().correspondingVariable.model()
}
