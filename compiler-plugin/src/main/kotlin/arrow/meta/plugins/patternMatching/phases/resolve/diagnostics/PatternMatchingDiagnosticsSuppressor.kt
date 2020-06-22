package arrow.meta.plugins.patternMatching.phases.resolve.diagnostics

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun CompilerContext.suppressUnresolvedReference(diagnostic: Diagnostic): Boolean =
  diagnostic.factory == Errors.UNRESOLVED_REFERENCE &&
    diagnostic.safeAs<DiagnosticWithParameters1<KtNameReferenceExpression, KtNameReferenceExpression>>()?.let { diagnosticWithParameters ->
      Log.Verbose({ "suppressUnresolvedReference: $this" }) {
        diagnosticWithParameters.psiElement.text == "_"
      }
    } == true
