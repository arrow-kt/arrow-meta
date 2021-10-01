package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface ClassifierDescriptorWithTypeParameters : ClassifierDescriptor, DeclarationDescriptorWithVisibility,
  MemberDescriptor {
  val isInner: Boolean

  val declaredTypeParameters: List<TypeParameterDescriptor>
}
