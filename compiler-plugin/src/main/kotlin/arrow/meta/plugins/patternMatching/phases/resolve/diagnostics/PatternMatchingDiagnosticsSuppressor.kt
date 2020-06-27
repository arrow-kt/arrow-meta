package arrow.meta.plugins.patternMatching.phases.resolve.diagnostics

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.patternMatching.PATTERN_EXPRESSION_BODY_PARAMS
import arrow.meta.plugins.patternMatching.PATTERN_EXPRESSION_CAPTURED_PARAMS
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun CompilerContext.suppressUnresolvedReference(diagnostic: Diagnostic): Boolean =
  diagnostic.factory == Errors.UNRESOLVED_REFERENCE &&
    diagnostic.safeAs<DiagnosticWithParameters1<KtNameReferenceExpression, KtNameReferenceExpression>>()?.let { diagnosticWithParameters ->
      Log.Verbose({ "suppressUnresolvedReference: $this" }) {
        val bindingTrace = componentProvider?.get<BindingTrace>()!!

          bindingTrace[PATTERN_EXPRESSION_CAPTURED_PARAMS, diagnosticWithParameters.psiElement] == true
          || bindingTrace[PATTERN_EXPRESSION_BODY_PARAMS, diagnosticWithParameters.psiElement] == true
      }
    } == true

fun CompilerContext.suppressUnderscoreUsageWithoutBackticks(diagnostic: Diagnostic): Boolean =
  diagnostic.factory == Errors.UNDERSCORE_USAGE_WITHOUT_BACKTICKS

fun CompilerContext.suppressExpressionExpectedPackageFound(diagnostic: Diagnostic): Boolean =
  diagnostic.factory == Errors.EXPRESSION_EXPECTED_PACKAGE_FOUND
