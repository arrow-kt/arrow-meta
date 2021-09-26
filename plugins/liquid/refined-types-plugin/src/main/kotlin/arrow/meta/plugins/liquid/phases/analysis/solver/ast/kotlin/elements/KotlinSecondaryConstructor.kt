package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ConstructorDelegationCall
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

fun interface KotlinSecondaryConstructor : SecondaryConstructor, KotlinConstructor<SecondaryConstructor> {
  override fun impl(): KtSecondaryConstructor
  override fun getDelegationCall(): ConstructorDelegationCall? =
    impl().getDelegationCallOrNull()?.model()
}
