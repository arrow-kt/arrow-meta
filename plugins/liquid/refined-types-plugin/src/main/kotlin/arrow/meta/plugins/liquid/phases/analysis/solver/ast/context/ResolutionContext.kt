package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor

interface ResolutionContext {
  val types: Types
  fun reportErrorsParsingPredicate(element: Element, msg: String)
  fun type(typeReference: TypeReference?): Type?
  fun reportUnsatCallPre(element: Element, msg: String)
  fun Element.constraintsDSLElements(): List<Element>
  fun reportInconsistentBodyPre(declaration: Element, msg: String)
  fun reportUnsatBodyPost(declaration: Element, msg: String)
  fun reportInconsistentCallPost(expression: Element, msg: String)
  fun reportInconsistentConditions(expression: Element, msg: String)
  fun reportInconsistentInvariants(expression: Element, msg: String)
  fun reportUnsatInvariants(expression: Element, msg: String)
  fun reportLiskovProblem(expression: Element, msg: String)

  val module: ModuleDescriptor
}
