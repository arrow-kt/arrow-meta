package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeArgumentList
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeProjection
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeArgumentList

fun interface KotlinTypeArgumentList : TypeArgumentList {
  fun impl(): KtTypeArgumentList
  override val arguments: List<TypeProjection>
    get() = impl().arguments.map { it.model() }
}
