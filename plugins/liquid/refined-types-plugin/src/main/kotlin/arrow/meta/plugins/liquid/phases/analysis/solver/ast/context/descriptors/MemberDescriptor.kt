package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface MemberDescriptor : DeclarationDescriptor {
  val modality: Modality
  val visibility: Visibility
  val isExpect: Boolean
  val isActual: Boolean
  val isExternal: Boolean
}
