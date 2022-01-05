package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinDefaultValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinExpressionValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinReceiverValue
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.calls.model.VarargValueArgument
import org.jetbrains.kotlin.resolve.calls.util.getReceiverExpression

class KotlinResolvedCall(
  val impl:
    org.jetbrains.kotlin.resolve.calls.model.ResolvedCall<
      out org.jetbrains.kotlin.descriptors.CallableDescriptor>
) : ResolvedCall {

  fun impl():
    org.jetbrains.kotlin.resolve.calls.model.ResolvedCall<
      out org.jetbrains.kotlin.descriptors.CallableDescriptor> = impl

  override val callElement: Element
    get() = impl().call.callElement.model()

  override fun getReceiverExpression(): Expression? = impl().getReceiverExpression()?.model()

  override fun getReturnType(): Type = KotlinType(impl().getReturnType())

  override val dispatchReceiver: ReceiverValue?
    get() = impl().dispatchReceiver?.let { KotlinReceiverValue(it) }

  override val extensionReceiver: ReceiverValue?
    get() = impl().extensionReceiver?.let { KotlinReceiverValue(it) }

  override val resultingDescriptor: CallableDescriptor
    get() = impl().resultingDescriptor.model()

  override val valueArguments: Map<ValueParameterDescriptor, ResolvedValueArgument>
    get() =
      impl()
        .valueArguments
        .map { (param, resolvedArg) ->
          val p: ValueParameterDescriptor = param.model()
          val a: ResolvedValueArgument =
            when (resolvedArg) {
              is DefaultValueArgument -> KotlinDefaultValueArgument(resolvedArg)
              is VarargValueArgument -> KotlinResolvedValueArgument(resolvedArg)
              is ExpressionValueArgument -> KotlinExpressionValueArgument(resolvedArg)
              else -> TODO()
            }
          p to a
        }
        .toMap()

  override val typeArguments: Map<TypeParameterDescriptor, Type>
    get() = TODO("Not yet implemented")
}
