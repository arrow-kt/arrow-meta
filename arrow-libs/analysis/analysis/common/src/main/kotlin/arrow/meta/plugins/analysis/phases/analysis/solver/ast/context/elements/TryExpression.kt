package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TryExpression : Expression {
  val tryBlock: BlockExpression
  val catchClauses: List<CatchClause>
  val finallyBlock: FinallySection?
}
