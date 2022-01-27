package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Variance
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.types.Variance.INVARIANT
import org.jetbrains.kotlin.types.Variance.IN_VARIANCE
import org.jetbrains.kotlin.types.Variance.OUT_VARIANCE

class KotlinTypeParameter(val impl: KtTypeParameter) : TypeParameter, KotlinNamedDeclaration {
  override fun impl(): KtTypeParameter = impl
  override val variance: Variance
    get() =
      when (impl().variance) {
        INVARIANT -> Variance.Invariant
        IN_VARIANCE -> Variance.In
        OUT_VARIANCE -> Variance.Out
      }
  override val extendsBounds: List<TypeReference>
    get() = listOfNotNull(impl().extendsBound?.model())
}
