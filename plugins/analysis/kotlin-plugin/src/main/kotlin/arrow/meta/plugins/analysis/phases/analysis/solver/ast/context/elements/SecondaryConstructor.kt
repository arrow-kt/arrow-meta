package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface SecondaryConstructor : Constructor<SecondaryConstructor> {
  fun getDelegationCall(): ConstructorDelegationCall?
}
