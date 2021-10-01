package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeParameter {
  val variance: Variance
  val extendsBound: TypeReference?
}
