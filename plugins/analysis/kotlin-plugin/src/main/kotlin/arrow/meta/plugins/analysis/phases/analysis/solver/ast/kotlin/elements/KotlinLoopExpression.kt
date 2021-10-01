package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LoopExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtLoopExpression

fun interface KotlinLoopExpression : LoopExpression, KotlinExpression {
  override fun impl(): KtLoopExpression
  override val body: Expression?
    get() = impl().body?.model()
}
