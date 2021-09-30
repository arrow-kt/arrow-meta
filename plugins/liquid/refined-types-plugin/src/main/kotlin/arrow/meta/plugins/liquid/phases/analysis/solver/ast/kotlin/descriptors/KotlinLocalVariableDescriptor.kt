package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.LocalVariableDescriptor

class KotlinLocalVariableDescriptor(val impl: org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor) : LocalVariableDescriptor, KotlinVariableDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor = impl
}
