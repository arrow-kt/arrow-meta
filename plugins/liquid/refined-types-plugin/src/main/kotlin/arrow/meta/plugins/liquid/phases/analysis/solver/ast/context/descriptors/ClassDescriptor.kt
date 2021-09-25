package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors


interface ClassDescriptor : DeclarationDescriptor, ClassifierDescriptorWithTypeParameters {

  val unsubstitutedMemberScope: MemberScope
  val constructors: Collection<ConstructorDescriptor>
  val companionObjectDescriptor: ClassDescriptor?
  val kind: ClassKind
  val isCompanionObject: Boolean
  val isData: Boolean
  val isInline: Boolean
  val isFun: Boolean
  val isValue: Boolean
  val thisAsReceiverParameter: ReceiverParameterDescriptor
  val unsubstitutedPrimaryConstructor: ConstructorDescriptor?
  val sealedSubclasses: Collection<ClassDescriptor>

  enum class ClassKind {
    CLASS, INTERFACE, ENUM_CLASS, ENUM_ENTRY, ANNOTATION_CLASS, OBJECT
  }

}
