package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry

class KotlinSuperTypeCallEntry(val impl: KtSuperTypeCallEntry) :
  SuperTypeCallEntry, KotlinSuperTypeListEntry, KotlinCallElement {
  override fun impl(): KtSuperTypeCallEntry = impl
}
