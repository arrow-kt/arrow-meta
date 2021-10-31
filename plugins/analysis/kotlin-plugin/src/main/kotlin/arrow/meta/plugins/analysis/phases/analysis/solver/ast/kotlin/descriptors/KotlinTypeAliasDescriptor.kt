package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeAliasConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeAliasDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType

class KotlinTypeAliasDescriptor(val impl: org.jetbrains.kotlin.descriptors.TypeAliasDescriptor) :
  TypeAliasDescriptor, KotlinClassifierDescriptorWithTypeParameters {
  override fun impl(): org.jetbrains.kotlin.descriptors.TypeAliasDescriptor = impl
  override val underlyingType: Type
    get() = KotlinType(impl().underlyingType)
  override val expandedType: Type
    get() = KotlinType(impl().expandedType)
  override val classDescriptor: ClassDescriptor?
    get() = impl().classDescriptor?.model()
  override val constructors: Collection<TypeAliasConstructorDescriptor>
    get() = impl().constructors.map { it.model() }
}
