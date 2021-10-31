package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface Constructor<T : Constructor<T>> : Function {
  fun getContainingClassOrObject(): ClassOrObject
}
