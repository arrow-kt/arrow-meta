package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.InitializerList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtInitializerList

class KotlinInitializerList(val impl: KtInitializerList) : InitializerList, KotlinElement {
  override fun impl() = impl

  override val entries: List<SuperTypeListEntry>
    get() = impl.initializers.map { it.model() }
}
