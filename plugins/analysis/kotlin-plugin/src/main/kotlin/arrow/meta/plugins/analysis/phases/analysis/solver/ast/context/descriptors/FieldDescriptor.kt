package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface FieldDescriptor : Annotated {
  val correspondingProperty: PropertyDescriptor
}
