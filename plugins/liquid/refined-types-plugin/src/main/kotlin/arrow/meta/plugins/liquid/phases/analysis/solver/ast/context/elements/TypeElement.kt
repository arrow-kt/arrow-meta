package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface TypeElement : Element {
  val typeArgumentsAsTypes: List<TypeReference>
}