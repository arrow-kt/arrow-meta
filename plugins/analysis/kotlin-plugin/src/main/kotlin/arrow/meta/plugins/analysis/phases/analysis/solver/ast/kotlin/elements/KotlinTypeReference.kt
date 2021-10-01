package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeElement
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeReference

class KotlinTypeReference(val impl: KtTypeReference) : TypeReference, KotlinAnnotated, KotlinAnnotationsContainer {
  override fun impl(): KtTypeReference = impl
  override val typeElement: TypeElement?
    get() = impl().typeElement?.model()
}
