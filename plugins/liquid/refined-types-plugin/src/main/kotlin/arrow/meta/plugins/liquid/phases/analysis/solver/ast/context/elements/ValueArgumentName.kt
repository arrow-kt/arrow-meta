package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ValueArgumentName {
  val asName: Name
  val referenceExpression: SimpleNameExpression?
}
