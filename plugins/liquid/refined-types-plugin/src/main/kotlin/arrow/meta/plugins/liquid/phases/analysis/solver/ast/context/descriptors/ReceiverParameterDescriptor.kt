package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface ReceiverParameterDescriptor : ParameterDescriptor {
  val value: ReceiverValue
}