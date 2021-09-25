package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassifierDescriptorWithTypeParameters
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor


fun interface KotlinClassDescriptor :
  ClassDescriptor,
  KotlinDeclarationDescriptor,
  KotlinClassifierDescriptorWithTypeParameters {

  override fun impl(): org.jetbrains.kotlin.descriptors.ClassDescriptor
  override val unsubstitutedMemberScope: MemberScope
    get() = KotlinMemberScope(impl().unsubstitutedMemberScope)
  override val constructors: Collection<ConstructorDescriptor>
    get() = TODO("Not yet implemented")
  override val companionObjectDescriptor: ClassDescriptor?
    get() = TODO("Not yet implemented")
  override val kind: ClassDescriptor.ClassKind
    get() = TODO("Not yet implemented")
  override val isCompanionObject: Boolean
    get() = TODO("Not yet implemented")
  override val isData: Boolean
    get() = TODO("Not yet implemented")
  override val isInline: Boolean
    get() = TODO("Not yet implemented")
  override val isFun: Boolean
    get() = TODO("Not yet implemented")
  override val isValue: Boolean
    get() = TODO("Not yet implemented")
  override val thisAsReceiverParameter: ReceiverParameterDescriptor
    get() = TODO("Not yet implemented")
  override val unsubstitutedPrimaryConstructor: ConstructorDescriptor?
    get() = TODO("Not yet implemented")
  override val sealedSubclasses: Collection<ClassDescriptor>
    get() = TODO("Not yet implemented")
  override val isInner: Boolean
    get() = TODO("Not yet implemented")
  override val declaredTypeParameters: List<TypeParameterDescriptor>
    get() = TODO("Not yet implemented")
}
