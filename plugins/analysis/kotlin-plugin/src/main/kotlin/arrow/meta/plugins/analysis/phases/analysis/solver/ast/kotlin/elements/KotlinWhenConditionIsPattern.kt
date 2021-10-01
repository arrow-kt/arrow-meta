package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenConditionIsPattern
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtWhenConditionIsPattern

fun interface KotlinWhenConditionIsPattern : WhenConditionIsPattern, KotlinWhenCondition {
  override fun impl(): KtWhenConditionIsPattern
  override val isNegated: Boolean
    get() = impl().isNegated
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
}
