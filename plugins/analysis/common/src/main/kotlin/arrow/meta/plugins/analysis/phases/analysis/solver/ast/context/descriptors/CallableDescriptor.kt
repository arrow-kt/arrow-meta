package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface CallableDescriptor : DeclarationDescriptor {
  val allParameters: List<ParameterDescriptor>
  val extensionReceiverParameter: ReceiverParameterDescriptor?
  val dispatchReceiverParameter: ReceiverParameterDescriptor?
  val typeParameters: List<TypeParameterDescriptor>
  val returnType: Type?
  val valueParameters: List<ValueParameterDescriptor>
  val overriddenDescriptors: Collection<CallableDescriptor>
}
