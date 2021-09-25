package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors


interface ConstructorDescriptor : FunctionDescriptor {
  val constructedClass: ClassDescriptor
  val isPrimary: Boolean
}
