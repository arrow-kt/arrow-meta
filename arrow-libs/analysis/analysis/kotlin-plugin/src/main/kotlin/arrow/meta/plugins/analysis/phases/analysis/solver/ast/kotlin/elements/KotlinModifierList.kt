package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Annotation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtModifierList

class KotlinModifierList(val impl: KtModifierList) : ModifierList, KotlinAnnotationsContainer {
  override fun impl(): KtModifierList = impl
  override val annotations: List<Annotation>
    get() = impl().annotations.map { it.model() }
  override val annotationEntries: List<AnnotationEntry>
    get() = impl().annotationEntries.map { it.model() }
}
