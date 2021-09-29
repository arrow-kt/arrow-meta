package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ParenthesizedExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtParenthesizedExpression

class KotlinParenthesizedExpression(override val impl: KtParenthesizedExpression) : ParenthesizedExpression, KotlinDefaultExpression(impl) {
  override val expression: Expression?
    get() = impl.expression?.model()
}
