package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface Declaration : Expression, ModifierListOwner {
  val name: String?
  val parents: List<Element>
}
