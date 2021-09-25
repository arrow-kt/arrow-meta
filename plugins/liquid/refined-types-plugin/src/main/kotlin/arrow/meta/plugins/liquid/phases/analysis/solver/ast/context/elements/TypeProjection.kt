package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface TypeProjection {
  val projectionKind: ProjectionKind
  val typeReference: TypeReference?
}
