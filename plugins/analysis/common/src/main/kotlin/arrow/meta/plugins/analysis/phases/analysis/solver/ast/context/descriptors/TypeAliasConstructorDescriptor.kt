package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface TypeAliasConstructorDescriptor : ConstructorDescriptor {
  val underlyingConstructorDescriptor: ConstructorDescriptor
  fun getContainingDeclaration(): TypeAliasDescriptor
}
