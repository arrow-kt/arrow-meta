package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ParameterList {
  val parameters: List<Parameter>
  val ownerFunction: DeclarationWithBody?
}
