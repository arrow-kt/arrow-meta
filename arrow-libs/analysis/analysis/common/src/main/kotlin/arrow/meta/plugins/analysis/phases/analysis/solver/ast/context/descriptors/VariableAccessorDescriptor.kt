package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface VariableAccessorDescriptor : FunctionDescriptor {
  val correspondingVariable: VariableDescriptorWithAccessors
}
