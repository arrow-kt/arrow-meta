package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinOperationReferenceExpression : KotlinSimpleNameExpression {
  fun isConventionOperator(): Boolean
}
