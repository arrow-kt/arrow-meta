package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface VariableDescriptorWithAccessors : VariableDescriptor {
  val getter: VariableAccessorDescriptor?

  val isDelegated: Boolean

  val setter: VariableAccessorDescriptor?
}
