@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.SimpleFunctionDescriptor
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

public class JavaSimpleFunctionDescriptor(ctx: AnalysisContext, impl: ExecutableElement) :
  SimpleFunctionDescriptor, JavaFunctionDescriptor(ctx, impl) {
  init {
    require(impl.kind == ElementKind.METHOD)
  }
}
