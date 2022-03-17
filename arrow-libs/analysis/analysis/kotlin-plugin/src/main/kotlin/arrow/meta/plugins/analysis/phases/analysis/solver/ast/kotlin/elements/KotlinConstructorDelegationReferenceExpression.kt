package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstructorDelegationReferenceExpression
import org.jetbrains.kotlin.psi.KtConstructorDelegationReferenceExpression

class KotlinConstructorDelegationReferenceExpression(
  val impl: KtConstructorDelegationReferenceExpression
) : ConstructorDelegationReferenceExpression, KotlinExpression, KotlinReferenceExpression {
  override fun impl(): KtConstructorDelegationReferenceExpression = impl
  override val isThis: Boolean
    get() = impl().isThis
}
