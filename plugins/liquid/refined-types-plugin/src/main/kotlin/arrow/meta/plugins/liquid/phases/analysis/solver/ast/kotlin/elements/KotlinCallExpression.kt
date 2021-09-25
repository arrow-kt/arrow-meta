package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.CallExpression
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtCallExpression

fun interface KotlinCallExpression : CallExpression, KotlinCallElement, KotlinReferenceExpression {
  override fun impl(): KtCallExpression
}

