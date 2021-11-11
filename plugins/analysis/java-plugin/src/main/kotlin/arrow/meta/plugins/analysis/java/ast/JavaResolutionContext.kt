@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.AnalysisMessages
import arrow.meta.plugins.analysis.java.ast.elements.JavaElement
import arrow.meta.plugins.analysis.java.ast.elements.JavaTypeReference
import arrow.meta.plugins.analysis.java.ast.elements.OurTreeVisitor
import arrow.meta.plugins.analysis.java.ast.elements.visitRecursively
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
import com.sun.source.tree.MethodInvocationTree
import com.sun.tools.javac.code.Lint
import com.sun.tools.javac.main.JavaCompiler
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.DiagnosticSource
import com.sun.tools.javac.util.JCDiagnostic

public class JavaResolutionContext(private val ctx: AnalysisContext) : ResolutionContext {
  override val types: Types =
    object : Types {
      override val nothingType: Type = ctx.symbolTable.botType.model(ctx)
    }

  override val module: ModuleDescriptor
    get() = ctx.elements.getModuleOf(ctx.resolver.topElement).model(ctx)

  override fun type(typeReference: TypeReference?): Type? =
    when (typeReference) {
      is JavaTypeReference -> ctx.resolver.resolveType(typeReference.underlyingTree)?.model(ctx)
      else -> null
    }

  override fun Element.constraintsDSLElements(): List<Element> {
    if (this !is JavaElement) return emptyList()

    val elements = mutableListOf<Element>()
    this.impl()
      .visitRecursively(
        object : OurTreeVisitor<Unit>(Unit) {
          override fun visitMethodInvocation(node: MethodInvocationTree, p: Unit?) {
            val calleeText = node.methodSelect?.toString()
            if (calleeText == "pre" || calleeText == "post") elements.add(node.model(ctx))
          }
        }
      )

    return elements.toList()
  }

  // not available in Java until we have records
  override fun backingPropertyForConstructorParameter(
    parameter: ValueParameterDescriptor
  ): PropertyDescriptor? = null

  override fun descriptorFor(fqName: FqName): List<DeclarationDescriptor> {
    val compiler = JavaCompiler.instance(ctx.context)
    return listOf(compiler.resolveBinaryNameOrIdent(fqName.name).model(ctx))
  }

  override fun descriptorFor(declaration: Declaration): DeclarationDescriptor? =
    (declaration as? JavaElement)?.let { ctx.resolver.resolve(it.impl())?.model(ctx) }

  private val diagnosticSource: DiagnosticSource
    get() =
      ctx.unit?.let { DiagnosticSource(it.sourceFile, ctx.logger) } ?: DiagnosticSource.NO_SOURCE

  private fun report(element: Element, builder: (JCTree) -> JCDiagnostic) {
    (element as? JavaElement)?.let { elt ->
      (elt.impl() as? JCTree)?.let { ctx.logger.report(builder(it)) }
    }
  }

  private fun reportError(element: Element, key: String, msg: String): Unit =
    report(element) {
      ctx.diagnostics.error(JCDiagnostic.DiagnosticFlag.MANDATORY, diagnosticSource, it, key, msg)
    }

  private fun reportWarning(element: Element, key: String, msg: String): Unit =
    report(element) {
      ctx.diagnostics.warning(Lint.LintCategory.PROCESSING, diagnosticSource, it, key, msg)
    }

  override fun reportErrorsParsingPredicate(element: Element, msg: String): Unit =
    reportError(element, AnalysisMessages.ErrorParsingPredicate, msg)

  override fun reportUnsatCallPre(element: Element, msg: String): Unit =
    reportError(element, AnalysisMessages.ErrorParsingPredicate, msg)

  override fun reportInconsistentBodyPre(declaration: Element, msg: String): Unit =
    reportError(declaration, AnalysisMessages.InconsistentBodyPre, msg)

  override fun reportUnsatBodyPost(declaration: Element, msg: String): Unit =
    reportError(declaration, AnalysisMessages.UnsatBodyPost, msg)

  override fun reportInconsistentCallPost(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.InconsistentCallPost, msg)

  override fun reportInconsistentConditions(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.InconsistentConditions, msg)

  override fun reportInconsistentInvariants(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.InconsistentInvariants, msg)

  override fun reportUnsatInvariants(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.UnsatInvariants, msg)

  override fun reportLiskovProblem(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.LiskovProblem, msg)

  override fun reportUnsupported(expression: Element, msg: String): Unit =
    reportWarning(expression, AnalysisMessages.UnsupportedElement, msg)
}
