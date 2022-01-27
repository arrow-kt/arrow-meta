package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LabelReferenceExpression
import org.jetbrains.kotlin.psi.KtLabelReferenceExpression

class KotlinLabelReferenceExpression(val impl: KtLabelReferenceExpression) :
  LabelReferenceExpression, KotlinSimpleNameExpression {
  override fun impl(): KtLabelReferenceExpression = impl
}
