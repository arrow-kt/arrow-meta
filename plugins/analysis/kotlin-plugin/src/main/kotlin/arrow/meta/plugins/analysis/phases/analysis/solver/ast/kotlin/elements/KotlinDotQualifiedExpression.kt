package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DotQualifiedExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

class KotlinDotQualifiedExpression(val impl: KtDotQualifiedExpression) :
  DotQualifiedExpression, KotlinQualifiedExpression {
  override fun impl(): KtDotQualifiedExpression = impl
}
