package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface PropertyAccessorDescriptor : VariableAccessorDescriptor {
  override val overriddenDescriptors: Collection<PropertyAccessorDescriptor>
}
