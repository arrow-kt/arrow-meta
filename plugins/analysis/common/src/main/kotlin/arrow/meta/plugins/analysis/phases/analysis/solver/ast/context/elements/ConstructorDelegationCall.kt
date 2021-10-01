package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface ConstructorDelegationCall : Element, CallElement {
  val isImplicit: Boolean
  val isCallToThis: Boolean
}
