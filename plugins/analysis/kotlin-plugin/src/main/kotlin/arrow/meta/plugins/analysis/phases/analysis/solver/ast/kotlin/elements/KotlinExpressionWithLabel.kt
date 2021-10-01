package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionWithLabel
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtExpressionWithLabel

fun interface KotlinExpressionWithLabel : ExpressionWithLabel, KotlinExpression {
  override fun impl(): KtExpressionWithLabel
  override fun getTargetLabel(): SimpleNameExpression? =
    impl().getTargetLabel()?.model()

  override fun getLabelName(): String? =
    impl().getLabelName()

  override fun getLabelNameAsName(): Name? =
    impl().getLabelNameAsName()?.let { Name(it.asString()) }
}
