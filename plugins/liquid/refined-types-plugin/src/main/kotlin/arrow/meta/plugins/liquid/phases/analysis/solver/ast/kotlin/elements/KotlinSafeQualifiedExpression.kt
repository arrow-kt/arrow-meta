package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

fun interface KotlinSafeQualifiedExpression : SafeQualifiedExpression, KotlinQualifiedExpression {
  override fun impl(): KtSafeQualifiedExpression
}
