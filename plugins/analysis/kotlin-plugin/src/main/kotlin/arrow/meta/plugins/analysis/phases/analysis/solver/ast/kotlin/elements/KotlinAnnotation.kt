package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Annotation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotationUseSiteTarget
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnnotation

class KotlinAnnotation(val impl: KtAnnotation) : Annotation, KotlinElement {
  override fun impl(): KtAnnotation = impl
  override val entries: List<AnnotationEntry>
    get() = impl().entries.map { it.model() }
  override val useSiteTarget: AnnotationUseSiteTarget?
    get() = impl().useSiteTarget?.model()
}
