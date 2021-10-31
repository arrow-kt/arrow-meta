package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName

interface Annotated {
  fun annotations(): Annotations

  val hasPreOrPostAnnotation: Boolean
    get() =
      annotations().run {
        hasAnnotation(FqName("arrow.analysis.Pre")) || hasAnnotation(FqName("arrow.analysis.Post"))
      }

  val hasPackageWithLawsAnnotation: Boolean
    get() = annotations().hasAnnotation(FqName("arrow.analysis.PackagesWithLaws"))

  val packageWithLawsAnnotation: AnnotationDescriptor?
    get() = annotations().findAnnotation(FqName("arrow.analysis.PackagesWithLaws"))
}
