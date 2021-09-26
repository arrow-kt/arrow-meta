package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DoWhileExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtExpression

fun interface KotlinDoWhileExpression : DoWhileExpression, KotlinWhileExpressionBase {
  override fun impl(): KtDoWhileExpression
}
