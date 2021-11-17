package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.LocalVariableDescriptor

class KotlinLocalVariableDescriptor(
  override val impl: org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
) : LocalVariableDescriptor, KotlinVariableDescriptor(impl) {
  override fun impl(): org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor = impl
}
