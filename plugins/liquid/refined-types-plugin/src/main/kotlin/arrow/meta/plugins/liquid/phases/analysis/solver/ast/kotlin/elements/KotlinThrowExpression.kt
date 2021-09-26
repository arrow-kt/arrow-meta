package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ThrowExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtThrowExpression

fun interface KotlinThrowExpression : ThrowExpression, KotlinExpression {
  override fun impl(): KtThrowExpression
  override val thrownExpression: Expression?
    get() = impl().thrownExpression?.model()
}
