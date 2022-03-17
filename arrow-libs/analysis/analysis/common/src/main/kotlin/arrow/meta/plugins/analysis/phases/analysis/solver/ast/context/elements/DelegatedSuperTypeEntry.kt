package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface DelegatedSuperTypeEntry : SuperTypeListEntry {
  val delegateExpression: Expression?
}
