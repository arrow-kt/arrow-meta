package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface MemberDescriptor : DeclarationDescriptor {
  val modality: Modality
  val visibility: Visibility
  val isExpect: Boolean
  val isActual: Boolean
  val isExternal: Boolean
}
