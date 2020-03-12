package arrow.meta.ide.plugins.initial

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.phases.resolve.metaSyntheticPackageFragmentProvider
import arrow.meta.invoke
import arrow.meta.plugins.higherkind.kindsTypeMismatch
import com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.cfg.ClassMissingCase
import org.jetbrains.kotlin.cfg.WhenMissingCase
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.initialIdeSetUp: Plugin
  get() = "Initial Ide Setup" {
    meta(
      addDiagnosticSuppressor { diagnostic ->
        LOG.debug("isSupressed: ${diagnostic.factory.name}: \n ${diagnostic.psiElement.text}")
        val result = diagnostic.suppressMetaDiagnostics()
        diagnostic.logSuppression(result)
        result
      },
      metaSyntheticPackageFragmentProvider,
      registerExtensionPoint(KotlinIndicesHelperExtension.Companion.extensionPointName,
        KotlinIndicesHelperExtension::class.java, ExtensionPoint.Kind.INTERFACE)
    )
  }

private fun Diagnostic.suppressMetaDiagnostics(): Boolean =
  suppressInvisibleMember() ||
    suppressNoElseInWhen() ||
    kindsTypeMismatch()

private fun Diagnostic.suppressInvisibleMember(): Boolean =
  factory == Errors.INVISIBLE_MEMBER

private fun Diagnostic.suppressNoElseInWhen(): Boolean {
  val result = factory == Errors.NO_ELSE_IN_WHEN && safeAs<DiagnosticWithParameters1<KtWhenExpression, List<WhenMissingCase>>>()?.let { diagnosticWithParameters ->
    val declaredCases = diagnosticWithParameters.psiElement.entries.flatMap { it.conditions.map { it.text } }.toSet()
    val missingCases = diagnosticWithParameters.a.filterIsInstance<ClassMissingCase>().map { it.toString() }.toSet()
    declaredCases.containsAll(missingCases)
  } ?: false
  return result
}

private fun Diagnostic.logSuppression(result: Boolean) {
  LOG.debug("Suppressing ${factory.name} on: `${psiElement.text}`: $result")
}
