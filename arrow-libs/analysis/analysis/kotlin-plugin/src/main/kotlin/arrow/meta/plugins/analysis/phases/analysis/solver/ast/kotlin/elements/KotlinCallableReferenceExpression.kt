package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableReferenceExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression

class KotlinCallableReferenceExpression(val impl: KtCallableReferenceExpression) :
  CallableReferenceExpression, KotlinDoubleColonExpression {
  override fun impl(): KtCallableReferenceExpression = impl

  override val callableReference: SimpleNameExpression
    get() = impl.callableReference.model()
}
