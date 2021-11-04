@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

public class JavaParameterDescriptor(ctx: AnalysisContext, private val impl: VariableElement) :
  ValueParameterDescriptor, JavaVariableDescriptor(ctx, impl) {
  init {
    require(impl.kind == ElementKind.PARAMETER || impl.kind == ElementKind.EXCEPTION_PARAMETER)
  }

  override val index: Int = (impl.enclosingElement as ExecutableElement).parameters.indexOf(impl)

  override val isCrossinline: Boolean = false
  override val isNoinline: Boolean = false
  override val varargElementType: Type? = null

  override fun declaresDefaultValue(): Boolean = impl.constantValue != null
  override val defaultValue: Expression? = TODO("Not yet implemented")
}
