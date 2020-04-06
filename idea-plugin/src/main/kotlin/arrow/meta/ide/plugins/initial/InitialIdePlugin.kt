package arrow.meta.ide.plugins.initial

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.higherkind.kindsTypeMismatch
import com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.cfg.ClassMissingCase
import org.jetbrains.kotlin.cfg.WhenMissingCase
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.initialIdeSetUp: IdePlugin
  get() = "Initial Ide Setup" {
    meta(
      metaPluginRegistrar,
      addDiagnosticSuppressor { diagnostic ->
        LOG.debug("isSupressed: ${diagnostic.factory.name}: \n ${diagnostic.psiElement.text}")
        val result = diagnostic.suppressMetaDiagnostics()
        diagnostic.logSuppression(result)
        result
      },
      registerExtensionPoint(KotlinIndicesHelperExtension.Companion.extensionPointName,
        KotlinIndicesHelperExtension::class.java, ExtensionPoint.Kind.INTERFACE)
    )
  }

/**
 * This extension registers a MetaPlugin for a given project.
 */
private val IdeMetaPlugin.metaPluginRegistrar: ExtensionPhase
  get() = addProjectLifecycle(
    initialize = { project ->
      val LOG = Logger.getInstance("#arrow.metaProjectRegistrarForProject:${project.name}")
      LOG.info("beforeProjectLoaded:${project.name}")
      val start = System.currentTimeMillis()
      val configuration = CompilerConfiguration()
      registerMetaComponents(project, configuration)
      LOG.info("beforeProjectLoaded:${project.name} took ${System.currentTimeMillis() - start}ms")
    },
    dispose = {
      // TODO: make sure that all registered extensions are disposed
    }
  )

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
