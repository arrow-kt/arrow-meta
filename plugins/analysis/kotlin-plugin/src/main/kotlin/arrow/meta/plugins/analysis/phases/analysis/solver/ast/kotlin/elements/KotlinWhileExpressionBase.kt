package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhileExpressionBase
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtWhileExpressionBase

fun interface KotlinWhileExpressionBase : WhileExpressionBase, KotlinLoopExpression {
  override fun impl(): KtWhileExpressionBase
  override val condition: Expression?
    get() = impl().condition?.model()
}
