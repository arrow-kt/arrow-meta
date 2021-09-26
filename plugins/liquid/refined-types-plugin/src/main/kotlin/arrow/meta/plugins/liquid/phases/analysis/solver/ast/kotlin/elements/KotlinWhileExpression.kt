package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.WhileExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

fun interface KotlinWhileExpression : WhileExpression, KotlinWhileExpressionBase {
  override fun impl(): KtWhileExpression
}
