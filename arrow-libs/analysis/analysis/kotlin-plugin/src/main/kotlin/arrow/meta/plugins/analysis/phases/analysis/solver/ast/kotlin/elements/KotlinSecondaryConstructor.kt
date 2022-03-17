package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstructorDelegationCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

class KotlinSecondaryConstructor(val impl: KtSecondaryConstructor) :
  SecondaryConstructor, KotlinConstructor<SecondaryConstructor> {
  override fun impl(): KtSecondaryConstructor = impl
  override fun getDelegationCall(): ConstructorDelegationCall? =
    impl().getDelegationCallOrNull()?.model()
}
