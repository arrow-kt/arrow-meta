package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Constructor<T : Constructor<T>> :
  Function {
  fun getContainingClassOrObject(): ClassOrObject
}
