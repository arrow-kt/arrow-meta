package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinTypeConstraint {
  val subjectTypeParameterName: KotlinSimpleNameExpression?
  val boundTypeReference: KotlinTypeReference?
}
