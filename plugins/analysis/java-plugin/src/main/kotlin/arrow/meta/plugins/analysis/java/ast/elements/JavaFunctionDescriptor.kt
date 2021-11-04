@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeKind

public open class JavaFunctionDescriptor(ctx: AnalysisContext, impl: ExecutableElement) :
  FunctionDescriptor, JavaMemberDescriptor(ctx, impl) {

  override val isOperator: Boolean = false
  override val isInfix: Boolean = false
  override val isInline: Boolean = false
  override val isTailrec: Boolean = false
  override val isSuspend: Boolean = false
  override val kind: CallableMemberDescriptor.Kind = CallableMemberDescriptor.Kind.DECLARATION

  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  final override val dispatchReceiverParameter: ReceiverParameterDescriptor? =
    when (impl.receiverType.kind) {
      TypeKind.NONE -> null
      else -> JavaReceiverParameterDescriptor(ctx, impl.receiverType, impl)
    }
  override val typeParameters: List<TypeParameterDescriptor> =
    impl.typeParameters.map { it.model(ctx) }
  override val returnType: Type? = impl.returnType.model(ctx)
  final override val valueParameters: List<ValueParameterDescriptor> =
    impl.parameters.map { it.model(ctx) }
  override val allParameters: List<ParameterDescriptor> =
    listOfNotNull(dispatchReceiverParameter) + valueParameters

  override val overriddenDescriptors: Collection<CallableDescriptor> = TODO("Not yet implemented")
}
