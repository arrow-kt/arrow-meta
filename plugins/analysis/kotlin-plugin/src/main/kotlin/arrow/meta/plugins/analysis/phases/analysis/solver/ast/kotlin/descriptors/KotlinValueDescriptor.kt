package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinValueDescriptor : ValueDescriptor, KotlinCallableDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.ValueDescriptor
  override val type: Type
    get() = KotlinType(impl().type)
}
