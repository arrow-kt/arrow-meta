package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Types

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
  fun reportUnsupported(expression: Element, msg: String)
  fun reportAnalysisException(element: Element, msg: String)
  fun descriptorFor(fqName: FqName): List<DeclarationDescriptor>
  fun descriptorFor(declaration: Declaration): DeclarationDescriptor?
  fun backingPropertyForConstructorParameter(
    parameter: ValueParameterDescriptor
  ): PropertyDescriptor?

  val module: ModuleDescriptor
}
