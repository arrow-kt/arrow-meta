package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DoWhileExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression

fun interface KotlinDoWhileExpression : DoWhileExpression, KotlinWhileExpressionBase {
  override fun impl(): KtDoWhileExpression
}
