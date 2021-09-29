package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

class KotlinSafeQualifiedExpression(val impl: KtSafeQualifiedExpression) : SafeQualifiedExpression,
  KotlinQualifiedExpression {
  override fun impl(): KtSafeQualifiedExpression = impl
}
