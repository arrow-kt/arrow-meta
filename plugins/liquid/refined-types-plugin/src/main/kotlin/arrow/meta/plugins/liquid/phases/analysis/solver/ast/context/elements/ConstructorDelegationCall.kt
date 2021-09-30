package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ConstructorDelegationCall : Element, CallElement {
  val isImplicit: Boolean
  val isCallToThis: Boolean
}
