package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeArgumentList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeProjection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeArgumentList

class KotlinTypeArgumentList(val impl: KtTypeArgumentList) : TypeArgumentList, KotlinElement {
  override fun impl(): KtTypeArgumentList = impl
  override val arguments: List<TypeProjection>
    get() = impl().arguments.map { it.model() }
}
