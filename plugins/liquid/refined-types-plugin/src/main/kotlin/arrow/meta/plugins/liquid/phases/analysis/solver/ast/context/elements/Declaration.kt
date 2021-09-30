package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Declaration : Expression, ModifierListOwner {
  val name: String?
  val parents: List<Element>
}
