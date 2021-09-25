package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors


interface VariableDescriptorWithAccessors :VariableDescriptor {
  val getter: VariableAccessorDescriptor?

  val isDelegated: Boolean

  val setter: VariableAccessorDescriptor?
}

