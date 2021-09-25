package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ParameterList {
  val parameters: List<Parameter>
  val ownerFunction: DeclarationWithBody?
}
