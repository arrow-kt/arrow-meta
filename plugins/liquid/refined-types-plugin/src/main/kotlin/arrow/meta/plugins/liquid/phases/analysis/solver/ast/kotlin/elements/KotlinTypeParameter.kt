package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinTypeParameter {
  val variance: KotlinVariance
  val extendsBound: KotlinTypeReference?
}
