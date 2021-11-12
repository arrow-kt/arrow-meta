@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.java.ast.types.allSupertypes
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

public open class JavaFunctionDescriptor(
  private val ctx: AnalysisContext,
  private val impl: ExecutableElement
) : FunctionDescriptor, JavaMemberDescriptor(ctx, impl) {

  override val isOperator: Boolean = false
  override val isInfix: Boolean = false
  override val isInline: Boolean = false
  override val isTailrec: Boolean = false
  override val isSuspend: Boolean = false
  override val kind: CallableMemberDescriptor.Kind = CallableMemberDescriptor.Kind.DECLARATION

  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  override val dispatchReceiverParameter: ReceiverParameterDescriptor?
    get() =
      when (impl.receiverType?.kind) {
        null, TypeKind.NONE -> null
        else -> JavaReceiverParameterDescriptor(ctx, impl.receiverType, impl)
      }
  override val typeParameters: List<TypeParameterDescriptor>
    get() = impl.typeParameters.map { it.model(ctx) }
  override val returnType: Type?
    get() = impl.returnType?.modelCautious(ctx)
  override val valueParameters: List<ValueParameterDescriptor>
    get() = impl.parameters.map { it.model(ctx) }
  override val allParameters: List<ParameterDescriptor>
    get() = listOfNotNull(dispatchReceiverParameter) + valueParameters

  override val overriddenDescriptors: Collection<CallableDescriptor>
    get() =
      when (val klass = impl.enclosingClass) {
        null -> emptyList()
        else ->
          klass.asType().allSupertypes(ctx).flatMap { parent ->
            ctx
              .types
              .asElement(parent)
              ?.enclosedElements
              .orEmpty()
              .filterIsInstance<ExecutableElement>()
              .filter { methodInParent -> ctx.elements.overrides(impl, methodInParent, klass) }
              .map { it.model(ctx) }
          }
      }
}
