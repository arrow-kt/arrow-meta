package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeEntry
import org.jetbrains.kotlin.psi.KtSuperTypeEntry

class KotlinSuperTypeEntry(val impl: KtSuperTypeEntry) : SuperTypeEntry, KotlinSuperTypeListEntry {
  override fun impl(): KtSuperTypeEntry = impl
}
