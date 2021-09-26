package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry

fun interface KotlinSuperTypeCallEntry : SuperTypeCallEntry, KotlinSuperTypeListEntry, KotlinCallElement {
  override fun impl(): KtSuperTypeCallEntry
}
