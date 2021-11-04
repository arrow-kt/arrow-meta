@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

public open class JavaVariableDescriptor(ctx: AnalysisContext, impl: VariableElement) :
  VariableDescriptor, JavaValueDescriptor(ctx, impl) {

  override val isVar: Boolean =
    when (impl.kind) {
      ElementKind.LOCAL_VARIABLE, ElementKind.FIELD -> true
      else -> false
    }
  override val isConst: Boolean = impl.constantValue != null
  override val isLateInit: Boolean = false
}
