package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ReferenceExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression

fun interface KotlinCallableReferenceExpression : ReferenceExpression, KotlinDoubleColonExpression {
  override fun impl(): KtCallableReferenceExpression
}
