package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface VariableDescriptor : ValueDescriptor {
  val isVar: Boolean
  val isConst: Boolean
  val isLateInit: Boolean
}
