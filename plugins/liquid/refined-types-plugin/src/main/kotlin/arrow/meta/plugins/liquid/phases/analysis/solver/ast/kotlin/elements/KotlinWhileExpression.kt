package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.WhileExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

class KotlinWhileExpression(val impl: KtWhileExpression) : WhileExpression, KotlinWhileExpressionBase {
  override fun impl(): KtWhileExpression = impl
}
