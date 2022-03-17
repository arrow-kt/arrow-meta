package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSuperTypeList

class KotlinSuperTypeList(val impl: KtSuperTypeList) : SuperTypeList, KotlinElement {
  override fun impl() = impl

  override val entries: List<SuperTypeListEntry>
    get() = impl.entries.map { it.model() }
}
