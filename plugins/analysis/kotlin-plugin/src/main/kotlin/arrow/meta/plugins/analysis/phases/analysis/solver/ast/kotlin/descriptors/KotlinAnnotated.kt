package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotated
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations

fun interface KotlinAnnotated : Annotated {
  fun impl(): org.jetbrains.kotlin.descriptors.annotations.Annotated
  override fun annotations(): Annotations = KotlinAnnotations(impl().annotations)
}
