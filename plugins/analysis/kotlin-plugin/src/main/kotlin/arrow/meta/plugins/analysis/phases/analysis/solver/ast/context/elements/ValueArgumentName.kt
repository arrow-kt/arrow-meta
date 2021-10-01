package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ValueArgumentName {
  val asName: Name
  val referenceExpression: SimpleNameExpression?
}
