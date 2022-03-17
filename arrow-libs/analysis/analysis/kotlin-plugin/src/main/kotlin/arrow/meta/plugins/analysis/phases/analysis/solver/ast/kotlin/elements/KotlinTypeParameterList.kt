package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeParameterList

class KotlinTypeParameterList(val impl: KtTypeParameterList) : TypeParameterList, KotlinElement {
  override fun impl(): KtTypeParameterList = impl
  override val parameters: List<TypeParameter>
    get() = impl().parameters.map { it.model() }
}
