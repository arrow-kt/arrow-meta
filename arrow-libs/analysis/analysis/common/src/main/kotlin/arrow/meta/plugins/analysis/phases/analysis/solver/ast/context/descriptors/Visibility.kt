package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface Visibility {
  val isPublicAPI: Boolean
  val name: String
}
