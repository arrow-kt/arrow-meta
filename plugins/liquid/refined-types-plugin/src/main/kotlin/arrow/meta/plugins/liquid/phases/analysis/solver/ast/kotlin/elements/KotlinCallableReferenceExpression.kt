package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ReferenceExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression

fun interface KotlinCallableReferenceExpression : ReferenceExpression, KotlinDoubleColonExpression {
  override fun impl(): KtCallableReferenceExpression
}
