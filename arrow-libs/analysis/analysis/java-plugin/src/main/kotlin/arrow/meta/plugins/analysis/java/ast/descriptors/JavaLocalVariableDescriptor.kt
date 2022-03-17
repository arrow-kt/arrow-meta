@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.LocalVariableDescriptor
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

public class JavaLocalVariableDescriptor(ctx: AnalysisContext, impl: VariableElement) :
  LocalVariableDescriptor, JavaVariableDescriptor(ctx, impl) {
  init {
    require(impl.kind == ElementKind.LOCAL_VARIABLE)
  }
}
