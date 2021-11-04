@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import com.sun.tools.javac.code.Symbol
import javax.lang.model.element.VariableElement

public open class JavaValueDescriptor(ctx: AnalysisContext, impl: VariableElement) :
  ValueDescriptor, JavaMemberDescriptor(ctx, impl) {

  override val type: Type =
    when (impl) {
      is Symbol -> impl.type.model(ctx)
      else -> throw IllegalStateException("this element should be a symbol")
    }
  override val allParameters: List<ParameterDescriptor> = emptyList()
  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  override val dispatchReceiverParameter: ReceiverParameterDescriptor? = null
  override val typeParameters: List<TypeParameterDescriptor> = emptyList()
  override val returnType: Type? = null
  override val valueParameters: List<ValueParameterDescriptor> = emptyList()
  override val overriddenDescriptors: Collection<CallableDescriptor> = emptyList()
}
