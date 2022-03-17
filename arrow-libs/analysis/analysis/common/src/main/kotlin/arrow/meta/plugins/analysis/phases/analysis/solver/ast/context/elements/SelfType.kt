package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface SelfType : TypeElement {
  override val typeArgumentsAsTypes: List<TypeReference>
}
