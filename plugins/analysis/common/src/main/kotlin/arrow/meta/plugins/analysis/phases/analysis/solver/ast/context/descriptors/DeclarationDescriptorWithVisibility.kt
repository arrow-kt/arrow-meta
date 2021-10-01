package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface DeclarationDescriptorWithVisibility : DeclarationDescriptor {
  val visibility: Visibility
}
