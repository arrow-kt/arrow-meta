package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface Visibility {
  val isPublicAPI: Boolean
  val name: String
}
