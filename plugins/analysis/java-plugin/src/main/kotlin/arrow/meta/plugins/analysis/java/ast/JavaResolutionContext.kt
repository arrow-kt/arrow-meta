@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.Resolver
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
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

public class JavaResolutionContext(ctx: AnalysisContext, resolver: Resolver) : ResolutionContext {
  override val types: Types =
    object : Types {
      override val nothingType: Type = ctx.symbolTable.botType.model(ctx)
    }

  override val module: ModuleDescriptor = ctx.elements.getModuleOf(resolver.topElement).model(ctx)

  override fun reportErrorsParsingPredicate(element: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun type(typeReference: TypeReference?): Type? {
    TODO("Not yet implemented")
  }

  override fun reportUnsatCallPre(element: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun Element.constraintsDSLElements(): List<Element> {
    TODO("Not yet implemented")
  }

  override fun reportInconsistentBodyPre(declaration: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportUnsatBodyPost(declaration: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportInconsistentCallPost(expression: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportInconsistentConditions(expression: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportInconsistentInvariants(expression: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportUnsatInvariants(expression: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportLiskovProblem(expression: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun reportUnsupported(expression: Element, msg: String) {
    TODO("Not yet implemented")
  }

  override fun descriptorFor(fqName: FqName): List<DeclarationDescriptor> {
    TODO("Not yet implemented")
  }

  override fun descriptorFor(declaration: Declaration): DeclarationDescriptor? {
    TODO("Not yet implemented")
  }

  override fun backingPropertyForConstructorParameter(
    parameter: ValueParameterDescriptor
  ): PropertyDescriptor? {
    TODO("Not yet implemented")
  }
}
