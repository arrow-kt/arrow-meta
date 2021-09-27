package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.ClassKind.*


class KotlinClassDescriptor(
  val impl: org.jetbrains.kotlin.descriptors.ClassDescriptor
) :
  ClassDescriptor,
  KotlinDeclarationDescriptor,
  KotlinClassifierDescriptorWithTypeParameters {

  override fun impl(): org.jetbrains.kotlin.descriptors.ClassDescriptor = impl
  override fun annotations(): Annotations = KotlinAnnotations(impl().annotations)
  override fun getUnsubstitutedMemberScope(): MemberScope = KotlinMemberScope { impl().unsubstitutedMemberScope }
  override val constructors: Collection<ConstructorDescriptor>
    get() = impl().constructors.map { it.model() }
  override val companionObjectDescriptor: ClassDescriptor?
    get() = impl().companionObjectDescriptor?.model()
  override val kind: ClassDescriptor.ClassKind
    get() = when (impl().kind) {
      CLASS -> ClassDescriptor.ClassKind.CLASS
      INTERFACE -> ClassDescriptor.ClassKind.INTERFACE
      ENUM_CLASS -> ClassDescriptor.ClassKind.ENUM_CLASS
      ENUM_ENTRY -> ClassDescriptor.ClassKind.ENUM_ENTRY
      ANNOTATION_CLASS -> ClassDescriptor.ClassKind.ANNOTATION_CLASS
      OBJECT -> ClassDescriptor.ClassKind.OBJECT
    }
  override val isCompanionObject: Boolean
    get() = impl().isCompanionObject
  override val isData: Boolean
    get() = impl().isData
  override val isInline: Boolean
    get() = impl().isInline
  override val isFun: Boolean
    get() = impl().isFun
  override val isValue: Boolean
    get() = impl().isValue
  override val thisAsReceiverParameter: ReceiverParameterDescriptor
    get() = impl().thisAsReceiverParameter.model()
  override val unsubstitutedPrimaryConstructor: ConstructorDescriptor?
    get() = impl().unsubstitutedPrimaryConstructor?.model()
  override val sealedSubclasses: Collection<ClassDescriptor>
    get() = impl().sealedSubclasses.map { it.model() }
  override val isInner: Boolean
    get() = impl.isInner
  override val declaredTypeParameters: List<TypeParameterDescriptor>
    get() = impl().declaredTypeParameters.map { it.model() }
}
