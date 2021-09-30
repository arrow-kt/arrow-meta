package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface DelegatedSuperTypeEntry : SuperTypeListEntry {
  val delegateExpression: Expression?
}
