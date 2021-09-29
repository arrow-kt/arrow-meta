package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface VariableAccessorDescriptor : FunctionDescriptor {
  val correspondingVariable: VariableDescriptorWithAccessors
}
