package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeElement
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeReference

fun interface KotlinTypeReference : TypeReference, KotlinAnnotated, KotlinAnnotationsContainer {
  override fun impl(): KtTypeReference
  override val typeElement: TypeElement?
    get() = impl().typeElement?.model()
}
