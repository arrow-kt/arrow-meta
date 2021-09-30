package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface DeclarationDescriptorWithVisibility : DeclarationDescriptor {
  val visibility: Visibility
}
