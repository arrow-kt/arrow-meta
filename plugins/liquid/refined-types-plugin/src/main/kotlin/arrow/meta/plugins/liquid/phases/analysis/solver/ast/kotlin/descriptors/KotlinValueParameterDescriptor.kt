package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinValueParameterDescriptor :
  ValueParameterDescriptor,
  KotlinVariableDescriptor,
  KotlinParameterDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
  override val index: Int
    get() = impl().index
  override val isCrossinline: Boolean
    get() = impl().isCrossinline
  override val isNoinline: Boolean
    get() = impl().isNoinline
  override val varargElementType: Type?
    get() = impl().varargElementType?.let { KotlinType(it) }

  override fun declaresDefaultValue(): Boolean =
    impl().declaresDefaultValue()
}


