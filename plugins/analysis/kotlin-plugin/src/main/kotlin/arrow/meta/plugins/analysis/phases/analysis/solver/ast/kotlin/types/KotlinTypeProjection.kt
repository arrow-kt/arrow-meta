package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.TypeProjection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Variance

class KotlinTypeProjection(val impl: org.jetbrains.kotlin.types.TypeProjection) : TypeProjection {
  override val projectionKind: Variance
    get() = when (impl.projectionKind) {
      org.jetbrains.kotlin.types.Variance.IN_VARIANCE -> Variance.In
      org.jetbrains.kotlin.types.Variance.OUT_VARIANCE -> Variance.Out
      org.jetbrains.kotlin.types.Variance.INVARIANT -> Variance.Invariant
    }
  override val type: Type
    get() = KotlinType(impl.type)
  override val isStarProjection: Boolean
    get() = impl.isStarProjection
}
