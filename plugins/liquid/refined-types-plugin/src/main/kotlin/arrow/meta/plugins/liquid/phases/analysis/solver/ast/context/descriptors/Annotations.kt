package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName

interface Annotations {

  fun iterable(): Iterable<AnnotationDescriptor>

  val impl: Any

  fun findAnnotation(fqName: FqName): AnnotationDescriptor?

  fun hasAnnotation(fqName: FqName): Boolean

  fun isEmpty(): Boolean
}


