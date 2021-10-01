package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenCondition
import org.jetbrains.kotlin.psi.KtWhenCondition

fun interface KotlinWhenCondition : WhenCondition, KotlinElement {
  override fun impl(): KtWhenCondition
}
