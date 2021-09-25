package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName

fun interface KotlinAnnotations : Annotations {

  fun impl(): org.jetbrains.kotlin.descriptors.annotations.Annotations

  override fun iterator(): Iterator<AnnotationDescriptor> =
    iterator {
      yieldAll(impl().map { KotlinAnnotationDescriptor { it } })
    }

  override fun isEmpty(): Boolean =
    impl().isEmpty()

  override fun findAnnotation(fqName: FqName): AnnotationDescriptor? =
    impl().findAnnotation(org.jetbrains.kotlin.name.FqName(fqName.name))?.let { KotlinAnnotationDescriptor { it } }

  override fun hasAnnotation(fqName: FqName): Boolean =
    impl().hasAnnotation(org.jetbrains.kotlin.name.FqName(fqName.name))
}


