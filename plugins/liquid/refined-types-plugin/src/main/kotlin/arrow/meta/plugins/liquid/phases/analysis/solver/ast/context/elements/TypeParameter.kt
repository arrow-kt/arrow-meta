package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface TypeParameter {
  val variance: Variance
  val extendsBound: TypeReference?
}
