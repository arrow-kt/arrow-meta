package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface SecondaryConstructor : Constructor<SecondaryConstructor> {
  fun getDelegationCall(): ConstructorDelegationCall?
}
