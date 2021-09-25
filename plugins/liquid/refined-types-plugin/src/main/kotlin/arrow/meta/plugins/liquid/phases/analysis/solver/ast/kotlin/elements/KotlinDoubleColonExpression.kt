package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DoubleColonExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDoubleColonExpression

fun interface KotlinDoubleColonExpression : DoubleColonExpression, KotlinExpression {
  override fun impl(): KtDoubleColonExpression
  override val receiverExpression: Expression?
    get() = impl().receiverExpression?.model()
  override val hasQuestionMarks: Boolean
    get() = impl().hasQuestionMarks
  override val isEmptyLHS: Boolean
    get() = impl().isEmptyLHS
}
