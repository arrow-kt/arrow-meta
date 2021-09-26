package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.LabeledExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtLabeledExpression

fun interface KotlinLabeledExpression : LabeledExpression, KotlinExpressionWithLabel {
  override fun impl(): KtLabeledExpression
  override val baseExpression: Expression?
    get() = impl().baseExpression?.model()
}
