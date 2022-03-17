package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionIsPattern
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtWhenConditionIsPattern

class KotlinWhenConditionIsPattern(val impl: KtWhenConditionIsPattern) :
  WhenConditionIsPattern, KotlinWhenCondition {
  override fun impl(): KtWhenConditionIsPattern = impl
  override val isNegated: Boolean
    get() = impl().isNegated
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
}
