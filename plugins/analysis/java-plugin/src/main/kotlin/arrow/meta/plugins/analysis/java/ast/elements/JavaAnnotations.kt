@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import javax.lang.model.element.AnnotationMirror

public class JavaAnnotations(
  private val ctx: AnalysisContext,
  override val impl: List<AnnotationMirror>
) : Annotations {
  override fun isEmpty(): Boolean = impl.isEmpty()
  override fun iterable(): Iterable<AnnotationDescriptor> =
    impl.map { JavaAnnotationDescriptor(ctx, it) }

  override fun findAnnotation(fqName: FqName): AnnotationDescriptor? =
    iterable().firstOrNull { it.fqName == fqName }
  override fun hasAnnotation(fqName: FqName): Boolean = findAnnotation(fqName) != null
}
