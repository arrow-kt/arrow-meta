package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.InstanceExpressionWithLabel
import org.jetbrains.kotlin.psi.KtInstanceExpressionWithLabel

fun interface KotlinInstanceExpressionWithLabel :
  InstanceExpressionWithLabel, KotlinExpressionWithLabel {
  override fun impl(): KtInstanceExpressionWithLabel
}
