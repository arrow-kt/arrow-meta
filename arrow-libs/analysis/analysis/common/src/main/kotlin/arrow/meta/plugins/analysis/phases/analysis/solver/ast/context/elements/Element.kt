package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor

interface Element : PureElement {
  val text: String
  fun impl(): Any
  fun getResolvedCall(context: ResolutionContext): ResolvedCall?
  fun getVariableDescriptor(context: ResolutionContext): VariableDescriptor?
  fun parents(): List<Element>
  fun location(): CompilerMessageSourceLocation?
}
