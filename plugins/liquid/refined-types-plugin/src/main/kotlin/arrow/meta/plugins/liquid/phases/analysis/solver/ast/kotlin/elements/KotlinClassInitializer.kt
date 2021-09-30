package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ClassInitializer
import org.jetbrains.kotlin.psi.KtClassInitializer

class KotlinClassInitializer(val impl: KtClassInitializer) : ClassInitializer, KotlinAnonymousInitializer {
  override fun impl(): KtClassInitializer = impl
}
