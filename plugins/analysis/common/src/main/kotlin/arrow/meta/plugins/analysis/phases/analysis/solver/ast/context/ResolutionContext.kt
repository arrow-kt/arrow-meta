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
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds

interface ResolutionContext {
  val types: Types
  fun handleError(error: ErrorIds, element: Element, msg: String): Unit
  fun type(typeReference: TypeReference?): Type?
  fun Element.constraintsDSLElements(): List<Element>
  fun descriptorFor(fqName: FqName): List<DeclarationDescriptor>
  fun descriptorFor(declaration: Declaration): DeclarationDescriptor?
  fun backingPropertyForConstructorParameter(
    parameter: ValueParameterDescriptor
  ): PropertyDescriptor?

  val module: ModuleDescriptor
}
