package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThrowExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtThrowExpression

class KotlinThrowExpression(val impl: KtThrowExpression) : ThrowExpression, KotlinExpression {
  override fun impl(): KtThrowExpression = impl
  override val thrownExpression: Expression?
    get() = impl().thrownExpression?.model()
}
