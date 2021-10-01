package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface ReceiverParameterDescriptor : ParameterDescriptor {
  val value: ReceiverValue
}
