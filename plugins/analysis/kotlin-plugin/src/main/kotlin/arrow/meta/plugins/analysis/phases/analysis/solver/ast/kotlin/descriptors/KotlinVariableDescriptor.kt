package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor

fun interface KotlinVariableDescriptor : VariableDescriptor, KotlinValueDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.VariableDescriptor
  override val isVar: Boolean
    get() = impl().isVar
  override val isConst: Boolean
    get() = impl().isConst
  override val isLateInit: Boolean
    get() = impl().isLateInit
}
