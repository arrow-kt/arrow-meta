package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SuperTypeList
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSuperTypeList

fun interface KotlinSuperTypeList : SuperTypeList {
  fun impl(): KtSuperTypeList
  override val entries: List<SuperTypeListEntry>
    get() = impl().entries.map { it.model() }
}
