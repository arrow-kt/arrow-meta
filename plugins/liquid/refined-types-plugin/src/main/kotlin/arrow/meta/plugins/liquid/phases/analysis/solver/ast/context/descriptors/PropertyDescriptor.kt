package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface PropertyDescriptor : VariableDescriptorWithAccessors, CallableMemberDescriptor {
  val isSetterProjectedOut: Boolean
  val accessors: List<PropertyAccessorDescriptor>
  val backingField: FieldDescriptor?
  val delegateField: FieldDescriptor?
}
