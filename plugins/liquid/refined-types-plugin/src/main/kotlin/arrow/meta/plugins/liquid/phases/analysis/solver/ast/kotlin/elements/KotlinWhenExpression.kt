package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinWhenExpression : KotlinExpression {
  val entries: List<KotlinWhenEntry>
  val subjectVariable: KotlinProperty?
  val subjectExpression: KotlinExpression?
  val elseExpression: KotlinExpression?
}
