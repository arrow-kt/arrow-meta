package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeProjection {
  val projectionKind: ProjectionKind
  val typeReference: TypeReference?
}
