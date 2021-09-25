package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.CallElement
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors.KotlinReceiverValue
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors.KotlinResolvedValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.resolve.calls.callUtil.getReceiverExpression

fun interface KotlinResolvedCall : ResolvedCall {
  fun impl(): org.jetbrains.kotlin.resolve.calls.model.ResolvedCall<out org.jetbrains.kotlin.descriptors.CallableDescriptor>
  override val callElement: CallElement
    get() = impl().call.callElement.model()

  override fun getReceiverExpression(): Expression? =
    impl().getReceiverExpression()?.model()

  override fun getReturnType(): Type =
    KotlinType(impl().getReturnType())

  override val dispatchReceiver: ReceiverValue?
    get() = impl().dispatchReceiver?.let { KotlinReceiverValue { it } }

  override val extensionReceiver: ReceiverValue?
    get() = impl().extensionReceiver?.let { KotlinReceiverValue { it } }

  override val resultingDescriptor: CallableDescriptor
    get() = impl().resultingDescriptor.model()

  override val valueArguments: Map<ValueParameterDescriptor, ResolvedValueArgument>
    get() = impl().valueArguments.map { (param, resolvedArg) ->
      val p : ValueParameterDescriptor = param.model()
      val a : ResolvedValueArgument = KotlinResolvedValueArgument { resolvedArg }
      p to a
    }.toMap()

  override val typeArguments: Map<TypeParameterDescriptor, Type>
    get() = TODO("Not yet implemented")
}
