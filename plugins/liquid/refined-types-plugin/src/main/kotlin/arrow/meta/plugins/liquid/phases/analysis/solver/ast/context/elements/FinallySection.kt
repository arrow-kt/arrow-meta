package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface FinallySection : Element {
  val finalExpression: BlockExpression
}
