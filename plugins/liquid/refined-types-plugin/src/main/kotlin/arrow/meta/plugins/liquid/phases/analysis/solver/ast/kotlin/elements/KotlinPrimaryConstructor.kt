package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.PrimaryConstructor
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtPrimaryConstructor

class KotlinPrimaryConstructor(val impl: KtPrimaryConstructor) : PrimaryConstructor, KotlinConstructor<PrimaryConstructor> {
  override fun impl(): KtConstructor<*> = impl
}
