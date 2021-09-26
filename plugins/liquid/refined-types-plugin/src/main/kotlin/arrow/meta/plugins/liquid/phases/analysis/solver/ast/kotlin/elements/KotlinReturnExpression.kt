package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReturnExpression

fun interface KotlinReturnExpression : ReturnExpression, KotlinExpressionWithLabel {
  override fun impl(): KtReturnExpression
  override val returnedExpression: Expression?
    get() = impl().returnedExpression?.model()
  override val labeledExpression: Expression?
    get() = (impl().labeledExpression as? KtElement)?.model()
}
