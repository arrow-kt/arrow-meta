package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface PropertyAccessorDescriptor : VariableAccessorDescriptor {
  override val overriddenDescriptors: Collection<PropertyAccessorDescriptor>
}
