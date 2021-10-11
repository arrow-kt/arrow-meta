package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassifierDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinTypeConstructor : TypeConstructor {

  fun impl(): org.jetbrains.kotlin.types.TypeConstructor

  override val parameters: List<TypeParameterDescriptor>
    get() = impl().parameters.map { it.model() }
  override val supertypes: Collection<Type>
    get() = impl().supertypes.map { KotlinType(it) }
  override val isFinal: Boolean
    get() = impl().isFinal
  override val isDenotable: Boolean
    get() = impl().isDenotable
  override val declarationDescriptor: ClassifierDescriptor?
    get() = impl().declarationDescriptor?.model()
}
