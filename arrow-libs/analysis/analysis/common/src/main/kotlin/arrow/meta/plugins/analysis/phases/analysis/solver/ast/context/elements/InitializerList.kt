package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface InitializerList : Element {
  val entries: List<SuperTypeListEntry>
}
