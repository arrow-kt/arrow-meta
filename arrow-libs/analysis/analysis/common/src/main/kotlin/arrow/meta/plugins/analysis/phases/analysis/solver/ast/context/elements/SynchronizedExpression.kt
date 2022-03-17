package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface SynchronizedExpression : Expression {
  val subject: Expression
  val block: BlockExpression
}
