package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotationUseSiteTarget
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnnotationEntry

fun interface KotlinAnnotationEntry : AnnotationEntry, KotlinCallElement {
  override fun impl(): KtAnnotationEntry
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
  override val useSiteTarget: AnnotationUseSiteTarget?
    get() = impl().useSiteTarget?.model()
  override val shortName: Name?
    get() = impl().shortName?.asString()?.let { Name(it) }
}
