package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeElement : Element {
  val typeArgumentsAsTypes: List<TypeReference>
}
