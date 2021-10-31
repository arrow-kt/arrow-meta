package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface ClassDescriptor : DeclarationDescriptor, ClassifierDescriptorWithTypeParameters {

  val unsubstitutedMemberScope: MemberScope
  val staticScope: MemberScope
  val unsubstitutedInnerClassesScope: MemberScope
  val completeUnsubstitutedScope: MemberScope
    get() =
      CombinedMemberScope(
        listOf(unsubstitutedMemberScope, staticScope, unsubstitutedInnerClassesScope)
      )

  val constructors: Collection<ConstructorDescriptor>
  val companionObjectDescriptor: ClassDescriptor?
  val kind: ClassKind
  val isCompanionObject: Boolean
  val isData: Boolean
  val isInline: Boolean
  val isFun: Boolean
  val isValue: Boolean
  val isEnumEntry: Boolean
  val thisAsReceiverParameter: ReceiverParameterDescriptor
  val unsubstitutedPrimaryConstructor: ConstructorDescriptor?
  val sealedSubclasses: Collection<ClassDescriptor>
  val superTypes: Collection<Type>

  enum class ClassKind {
    CLASS,
    INTERFACE,
    ENUM_CLASS,
    ENUM_ENTRY,
    ANNOTATION_CLASS,
    OBJECT
  }
}
