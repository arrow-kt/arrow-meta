package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.WhenCondition
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.WhenEntry
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtWhenEntry

class KotlinWhenEntry(val impl: KtWhenEntry) : WhenEntry, KotlinElement {
  override fun impl(): KtWhenEntry = impl
  override val isElse: Boolean
    get() = impl().isElse
  override val expression: Expression?
    get() = impl().expression?.model()
  override val conditions: List<WhenCondition>
    get() = impl().conditions.map { it.model() }
}
