package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolvedCall

interface Element : PureElement {
  val text: String

  fun getResolvedCall(context: ResolutionContext): ResolvedCall?
  fun parents(): List<Element>
  fun location(): CompilerMessageSourceLocation?
}
