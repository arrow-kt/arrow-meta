@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

public class JavaConstructorDescriptor(ctx: AnalysisContext, impl: ExecutableElement) :
  ConstructorDescriptor, JavaFunctionDescriptor(ctx, impl) {
  init {
    require(impl.kind == ElementKind.CONSTRUCTOR)
  }

  override val constructedClass: ClassDescriptor = impl.returnType.model(ctx).descriptor!!
  override val isPrimary: Boolean = false
}
