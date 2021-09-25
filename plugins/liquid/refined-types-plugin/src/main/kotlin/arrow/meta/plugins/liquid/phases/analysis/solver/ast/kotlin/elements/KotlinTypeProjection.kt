package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinTypeProjection {
  val projectionKind: KotlinProjectionKind
  val typeReference: KotlinTypeReference?
}
