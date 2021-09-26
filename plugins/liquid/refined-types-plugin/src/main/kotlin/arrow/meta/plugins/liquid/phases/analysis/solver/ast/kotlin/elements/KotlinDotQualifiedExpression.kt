package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DotQualifiedExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

fun interface KotlinDotQualifiedExpression : DotQualifiedExpression, KotlinQualifiedExpression {
  override fun impl(): KtDotQualifiedExpression
}
