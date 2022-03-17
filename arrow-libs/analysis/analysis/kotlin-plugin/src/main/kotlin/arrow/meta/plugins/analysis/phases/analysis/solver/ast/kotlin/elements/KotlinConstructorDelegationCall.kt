package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall

class KotlinConstructorDelegationCall(val impl: KtConstructorDelegationCall) :
  ConstructorDelegationCall, KotlinElement, KotlinCallElement {
  override fun impl(): KtConstructorDelegationCall = impl
  override val isImplicit: Boolean
    get() = impl().isImplicit
  override val isCallToThis: Boolean
    get() = impl().isCallToThis
}
