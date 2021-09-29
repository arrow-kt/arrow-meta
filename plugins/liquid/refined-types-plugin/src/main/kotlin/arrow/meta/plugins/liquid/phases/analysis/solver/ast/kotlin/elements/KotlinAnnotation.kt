package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Annotation
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotationUseSiteTarget
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnnotation

fun interface KotlinAnnotation : Annotation {
  fun impl(): KtAnnotation
  override val entries: List<AnnotationEntry>
    get() =
      impl().entries.map {
        it.model()
      }
  override val useSiteTarget: AnnotationUseSiteTarget?
    get() =
      impl().useSiteTarget?.model()
}
