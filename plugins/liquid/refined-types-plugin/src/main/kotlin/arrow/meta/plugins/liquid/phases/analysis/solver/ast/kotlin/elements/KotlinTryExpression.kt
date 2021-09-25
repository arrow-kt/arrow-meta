package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinTryExpression : KotlinExpression {
  val tryBlock: KotlinBlockExpression
  val catchClauses: List<KotlinCatchClause>
  val finallyBlock: KotlinFinallySection?
}
