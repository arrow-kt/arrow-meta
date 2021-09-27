package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName

class KotlinAnnotations(override val impl: org.jetbrains.kotlin.descriptors.annotations.Annotations) : Annotations {

  override fun iterable(): Iterable<AnnotationDescriptor> =
    impl.map { KotlinAnnotationDescriptor(it) }

  override fun isEmpty(): Boolean =
    impl.isEmpty()

  override fun findAnnotation(fqName: FqName): AnnotationDescriptor? =
    impl.findAnnotation(org.jetbrains.kotlin.name.FqName(fqName.name))?.let { KotlinAnnotationDescriptor(it) }

  override fun hasAnnotation(fqName: FqName): Boolean =
    impl.hasAnnotation(org.jetbrains.kotlin.name.FqName(fqName.name))


}


