package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Annotated
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Annotation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnnotated

fun interface KotlinAnnotated : Annotated, KotlinElement {
  override fun impl(): KtAnnotated

  override fun getAnnotations(): List<Annotation> = impl().annotations.map { it.model() }

  override val annotationEntries: List<AnnotationEntry>
    get() = impl().annotationEntries.map { it.model() }
}
