package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types

interface TypeProjection {
  val projectionKind: Variance
  val type: Type
  val isStarProjection: Boolean
}
