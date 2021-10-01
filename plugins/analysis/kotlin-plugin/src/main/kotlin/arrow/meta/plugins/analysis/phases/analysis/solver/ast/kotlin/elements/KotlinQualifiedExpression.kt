package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.QualifiedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtQualifiedExpression

fun interface KotlinQualifiedExpression : QualifiedExpression, KotlinExpression {
  override fun impl(): KtQualifiedExpression
  override val receiverExpression: Expression
    get() = impl().receiverExpression.model()
  override val selectorExpression: Expression?
    get() = impl().selectorExpression?.model()
}
