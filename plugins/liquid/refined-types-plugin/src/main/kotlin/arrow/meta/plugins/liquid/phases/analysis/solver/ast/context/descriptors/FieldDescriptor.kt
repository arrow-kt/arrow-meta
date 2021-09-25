package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface FieldDescriptor : Annotated {
  val correspondingProperty: PropertyDescriptor
}

