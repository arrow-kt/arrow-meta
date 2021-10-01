
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class KotlinNameReferenceExpression(
  val impl: KtNameReferenceExpression
) : NameReferenceExpression, KotlinSimpleNameExpression {
  override fun impl(): KtNameReferenceExpression = impl
}
