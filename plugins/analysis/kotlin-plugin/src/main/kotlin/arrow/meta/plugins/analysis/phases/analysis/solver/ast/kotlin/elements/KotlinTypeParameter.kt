package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Variance
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.types.Variance.INVARIANT
import org.jetbrains.kotlin.types.Variance.IN_VARIANCE
import org.jetbrains.kotlin.types.Variance.OUT_VARIANCE

fun interface KotlinTypeParameter : TypeParameter {
  fun impl(): KtTypeParameter
  override val variance: Variance
    get() = when (impl().variance) {
      INVARIANT -> Variance.Invariant
      IN_VARIANCE -> Variance.In
      OUT_VARIANCE -> Variance.Out
    }
  override val extendsBound: TypeReference?
    get() = impl().extendsBound?.model()
}
