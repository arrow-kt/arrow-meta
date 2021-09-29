package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface ClassifierDescriptorWithTypeParameters : ClassifierDescriptor, DeclarationDescriptorWithVisibility,
  MemberDescriptor {
  val isInner: Boolean

  val declaredTypeParameters: List<TypeParameterDescriptor>
}
