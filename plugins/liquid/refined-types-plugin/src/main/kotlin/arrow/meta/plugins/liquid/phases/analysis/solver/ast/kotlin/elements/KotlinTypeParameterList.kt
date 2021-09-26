package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeParameterList
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeParameterList

fun interface KotlinTypeParameterList: TypeParameterList {
  fun impl(): KtTypeParameterList
  override val parameters: List<TypeParameter>
    get() = impl().parameters.map { it.model() }
}
