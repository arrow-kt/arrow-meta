package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface VariableDescriptor : ValueDescriptor {
  val isVar: Boolean
  val isConst: Boolean
  val isLateInit: Boolean
}
