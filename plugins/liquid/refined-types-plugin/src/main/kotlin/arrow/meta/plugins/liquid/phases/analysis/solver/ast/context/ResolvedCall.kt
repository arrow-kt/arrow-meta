package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.CallElement
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor

interface ResolvedCall {
  val callElement: CallElement
  fun getReceiverExpression(): Expression?
  fun getReturnType(): Type
  val dispatchReceiver: ReceiverValue?
  val extensionReceiver: ReceiverValue?
  val resultingDescriptor: CallableDescriptor
  val valueArguments: Map<ValueParameterDescriptor, ResolvedValueArgument>
  val typeArguments: Map<TypeParameterDescriptor, Type>
}
