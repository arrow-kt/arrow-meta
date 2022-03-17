package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import org.jetbrains.kotlin.psi.KtExpression

open class KotlinDefaultExpression(open val impl: KtExpression) : KotlinExpression {
  override fun impl(): KtExpression = impl
}
