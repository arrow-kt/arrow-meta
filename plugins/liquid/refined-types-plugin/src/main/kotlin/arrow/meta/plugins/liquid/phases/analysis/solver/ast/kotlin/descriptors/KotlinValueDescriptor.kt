package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ValueDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinValueDescriptor : ValueDescriptor, KotlinCallableDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.ValueDescriptor
  override val type: Type
    get() = KotlinType(impl().type)
}
