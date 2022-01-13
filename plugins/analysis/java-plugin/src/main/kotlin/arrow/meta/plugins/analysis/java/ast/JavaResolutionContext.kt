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
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import com.sun.source.tree.AssertTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.tools.javac.code.Lint
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.main.JavaCompiler
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.DiagnosticSource
import com.sun.tools.javac.util.JCDiagnostic

public class JavaResolutionContext(
  private val state: SolverState?,
  private val ctx: AnalysisContext
) : ResolutionContext {
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
            node.methodSelect?.toString()?.let { calleeText ->
              if (calleeText == "pre" ||
                  calleeText.endsWith(".pre") ||
                  calleeText == "post" ||
                  calleeText.endsWith(".post") ||
                  calleeText == "doNotLookAtArgumentsWhen" ||
                  calleeText.endsWith(".doNotLookAtArgumentsWhen")
              )
                elements.add(node.model(ctx))
            }
          }

          override fun visitAssert(node: AssertTree, p: Unit?) {
            elements.add(node.model(ctx))
          }
        }
      )

    return elements.toList()
  }

  override fun handleError(error: ErrorIds, element: Element, msg: String): Unit {
    when (error) {
      ErrorIds.Exception.IllegalState -> reportAnalysisException(element, msg)
      ErrorIds.Exception.OtherException -> reportAnalysisException(element, msg)
      ErrorIds.Inconsistency.InconsistentBodyPre -> reportInconsistentBodyPre(element, msg)
      ErrorIds.Inconsistency.InconsistentDefaultValues -> reportInconsistentBodyPre(element, msg)
      ErrorIds.Inconsistency.InconsistentConditions -> reportInconsistentConditions(element, msg)
      ErrorIds.Inconsistency.InconsistentCallPost -> reportInconsistentCallPost(element, msg)
      ErrorIds.Inconsistency.InconsistentInvariants -> reportInconsistentInvariants(element, msg)
      ErrorIds.Liskov.NotWeakerPrecondition -> reportLiskovProblem(element, msg)
      ErrorIds.Liskov.NotStrongerPostcondition -> reportLiskovProblem(element, msg)
      ErrorIds.Parsing.ErrorParsingPredicate -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Parsing.UnexpectedReference -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Parsing.UnexpectedFieldInitBlock -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.LawMustCallFunction -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.LawMustHaveParametersInOrder -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.SubjectWithoutName -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.CouldNotResolveSubject -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Unsatisfiability.UnsatCallPre -> reportUnsatCallPre(element, msg)
      ErrorIds.Unsatisfiability.UnsatBodyPost -> reportUnsatBodyPost(element, msg)
      ErrorIds.Unsatisfiability.UnsatInvariants -> reportUnsatInvariants(element, msg)
      ErrorIds.Unsupported.UnsupportedExpression -> reportUnsupported(element, msg)
    }
    state?.notifySarifReport(error, element, msg)
  }

  // not available in Java until we have records
  override fun backingPropertyForConstructorParameter(
    parameter: ValueParameterDescriptor
  ): PropertyDescriptor? = null

  override fun descriptorFor(fqName: FqName): List<DeclarationDescriptor> =
    JavaCompiler.instance(ctx.context)
      .resolveBinaryNameOrIdent(fqName.name)
      .takeIf { it.exists() }
      ?.let<Symbol, List<DeclarationDescriptor>> { listOf(it.model(ctx)) }
      .orEmpty()

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

  private fun reportErrorsParsingPredicate(element: Element, msg: String): Unit =
    reportError(element, AnalysisMessages.ErrorParsingPredicate, msg)

  private fun reportUnsatCallPre(element: Element, msg: String): Unit =
    reportError(element, AnalysisMessages.UnsatCallPre, msg)

  private fun reportInconsistentBodyPre(declaration: Element, msg: String): Unit =
    reportError(declaration, AnalysisMessages.InconsistentBodyPre, msg)

  private fun reportUnsatBodyPost(declaration: Element, msg: String): Unit =
    reportError(declaration, AnalysisMessages.UnsatBodyPost, msg)

  fun reportInconsistentCallPost(expression: Element, msg: String): Unit =
    reportWarning(expression, AnalysisMessages.InconsistentCallPost, msg)

  fun reportInconsistentConditions(expression: Element, msg: String): Unit =
    reportWarning(expression, AnalysisMessages.InconsistentConditions, msg)

  fun reportInconsistentInvariants(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.InconsistentInvariants, msg)

  fun reportUnsatInvariants(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.UnsatInvariants, msg)

  fun reportLiskovProblem(expression: Element, msg: String): Unit =
    reportError(expression, AnalysisMessages.LiskovProblem, msg)

  fun reportUnsupported(expression: Element, msg: String): Unit =
    reportWarning(expression, AnalysisMessages.UnsupportedElement, msg)

  fun reportAnalysisException(element: Element, msg: String): Unit =
    reportError(element, AnalysisMessages.AnalysisException, msg)
}
