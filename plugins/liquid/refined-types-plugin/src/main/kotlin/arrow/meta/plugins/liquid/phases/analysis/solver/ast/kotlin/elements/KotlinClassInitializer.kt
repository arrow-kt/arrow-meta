package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ClassInitializer
import org.jetbrains.kotlin.psi.KtClassInitializer

fun interface KotlinClassInitializer : ClassInitializer, KotlinAnonymousInitializer {
  override fun impl(): KtClassInitializer
}
