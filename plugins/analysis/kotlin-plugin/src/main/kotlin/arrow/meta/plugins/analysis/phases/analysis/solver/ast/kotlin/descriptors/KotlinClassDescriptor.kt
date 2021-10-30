package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.descriptors.ClassKind

class KotlinClassDescriptor(val impl: org.jetbrains.kotlin.descriptors.ClassDescriptor) :
  ClassDescriptor, KotlinDeclarationDescriptor, KotlinClassifierDescriptorWithTypeParameters {

  override fun impl(): org.jetbrains.kotlin.descriptors.ClassDescriptor = impl
  override fun annotations(): Annotations = KotlinAnnotations(impl().annotations)

  override val unsubstitutedMemberScope: MemberScope
    get() = KotlinMemberScope { impl.unsubstitutedMemberScope }
  override val staticScope: MemberScope
    get() = KotlinMemberScope { impl.staticScope }
  override val unsubstitutedInnerClassesScope: MemberScope
    get() = KotlinMemberScope { impl.unsubstitutedInnerClassesScope }

  override val constructors: Collection<ConstructorDescriptor>
    get() = impl().constructors.map { it.model() }
  override val companionObjectDescriptor: ClassDescriptor?
    get() = impl().companionObjectDescriptor?.model()
  override val kind: ClassDescriptor.ClassKind
    get() =
      when (impl().kind) {
        ClassKind.CLASS -> ClassDescriptor.ClassKind.CLASS
        ClassKind.INTERFACE -> ClassDescriptor.ClassKind.INTERFACE
        ClassKind.ENUM_CLASS -> ClassDescriptor.ClassKind.ENUM_CLASS
        ClassKind.ENUM_ENTRY -> ClassDescriptor.ClassKind.ENUM_ENTRY
        ClassKind.ANNOTATION_CLASS -> ClassDescriptor.ClassKind.ANNOTATION_CLASS
        ClassKind.OBJECT -> ClassDescriptor.ClassKind.OBJECT
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
  override val isEnumEntry: Boolean
    get() = impl().kind == ClassKind.ENUM_ENTRY
  override val thisAsReceiverParameter: ReceiverParameterDescriptor
    get() = impl().thisAsReceiverParameter.model()
  override val unsubstitutedPrimaryConstructor: ConstructorDescriptor?
    get() = impl().unsubstitutedPrimaryConstructor?.model()
  override val sealedSubclasses: Collection<ClassDescriptor>
    get() = impl().sealedSubclasses.map { it.model() }
  override val superTypes: Collection<Type>
    get() = impl.defaultType.constructor.supertypes.map { KotlinType(it) }
  override val isInner: Boolean
    get() = impl.isInner
  override val declaredTypeParameters: List<TypeParameterDescriptor>
    get() = impl().declaredTypeParameters.map { it.model() }
}
