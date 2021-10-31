package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.source.getPsi

class KotlinValueParameterDescriptor(
  val impl: org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
) : ValueParameterDescriptor, KotlinVariableDescriptor, KotlinParameterDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.ValueParameterDescriptor = impl
  override val index: Int
    get() = impl().index
  override val isCrossinline: Boolean
    get() = impl().isCrossinline
  override val isNoinline: Boolean
    get() = impl().isNoinline
  override val varargElementType: Type?
    get() = impl().varargElementType?.let { KotlinType(it) }

  override fun declaresDefaultValue(): Boolean = impl().declaresDefaultValue()
  override val defaultValue: Expression?
    get() = (impl.source.getPsi() as? KtParameter)?.defaultValue?.model()
}
