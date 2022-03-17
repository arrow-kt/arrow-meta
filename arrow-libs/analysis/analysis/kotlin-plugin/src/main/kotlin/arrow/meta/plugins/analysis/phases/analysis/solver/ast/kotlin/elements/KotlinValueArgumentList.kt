package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtValueArgumentList

class KotlinValueArgumentList(val impl: KtValueArgumentList) : ValueArgumentList, KotlinElement {
  override fun impl(): KtValueArgumentList = impl
  override val arguments: List<ValueArgument>
    get() = impl().arguments.map { it.model() }
}
