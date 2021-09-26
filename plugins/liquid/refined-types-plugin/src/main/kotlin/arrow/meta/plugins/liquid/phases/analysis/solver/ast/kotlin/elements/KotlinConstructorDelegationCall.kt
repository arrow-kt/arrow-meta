package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall

fun interface KotlinConstructorDelegationCall : ConstructorDelegationCall, KotlinElement, KotlinCallElement {
  override fun impl(): KtConstructorDelegationCall
  override val isImplicit: Boolean
    get() = impl().isImplicit
  override val isCallToThis: Boolean
    get() = impl().isCallToThis
}
