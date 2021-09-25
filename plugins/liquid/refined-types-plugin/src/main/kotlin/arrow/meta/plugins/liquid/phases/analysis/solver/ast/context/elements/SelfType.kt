package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface SelfType : TypeElement {
  override val typeArgumentsAsTypes: List<TypeReference>
}
