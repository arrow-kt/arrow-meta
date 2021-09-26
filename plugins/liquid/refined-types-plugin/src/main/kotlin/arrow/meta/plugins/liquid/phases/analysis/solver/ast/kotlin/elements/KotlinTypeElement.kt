package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeElement
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeElement

fun interface KotlinTypeElement : TypeElement, KotlinElement {
  override fun impl(): KtTypeElement
  override val typeArgumentsAsTypes: List<TypeReference>
    get() = impl().typeArgumentsAsTypes.map { it.model() }
}
