package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BreakExpression
import org.jetbrains.kotlin.psi.KtBreakExpression

fun interface KotlinBreakExpression : BreakExpression, KotlinExpressionWithLabel {
  override fun impl(): KtBreakExpression
}
