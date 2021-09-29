package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface TypeAliasConstructorDescriptor : ConstructorDescriptor {
  val underlyingConstructorDescriptor: ConstructorDescriptor
  fun getContainingDeclaration(): TypeAliasDescriptor
}
