package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SuperTypeEntry
import org.jetbrains.kotlin.psi.KtSuperTypeEntry

fun interface KotlinSuperTypeEntry : SuperTypeEntry, KotlinSuperTypeListEntry {
  override fun impl(): KtSuperTypeEntry
}
