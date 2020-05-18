package arrow.meta.ide.plugins.initial

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.plugins.external.ui.tooltip.MetaEditorMouseHoverPopupManager
import arrow.meta.ide.plugins.external.ui.tooltip.MetaTooltipController
import arrow.meta.ide.plugins.external.ui.tooltip.MetaTooltipRenderer
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.higherkind.kindsTypeMismatch
import com.intellij.codeInsight.hint.TooltipController
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
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
      toolTipController,
      addDiagnosticSuppressor { diagnostic ->
        LOG.debug("isSupressed: ${diagnostic.factory.name}: \n ${diagnostic.psiElement.text}")
        val result = diagnostic.suppressMetaDiagnostics()
        diagnostic.logSuppression(result)
        result
      },
      registerExtensionPoint(KotlinIndicesHelperExtension.Companion.extensionPointName,
        KotlinIndicesHelperExtension::class.java, ExtensionPoint.Kind.INTERFACE),
      replaceEditorMouseListeners
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
      registerMetaComponents(project, configuration, project.ctx())
      LOG.info("beforeProjectLoaded:${project.name} took ${System.currentTimeMillis() - start}ms")
    },
    dispose = {
      // TODO: make sure that all registered extensions are disposed
    }
  )

/**
 * Replaces TooltipController by our custom one that provides custom CSS support. Relies on [MetaTooltipRenderer] for
 * the rendering logic.
 *
 * TooltipController is what the open api uses to render tooltips for LineMarkers, so this effectively enables us to
 * show custom CSS for all Meta line marker tooltips.
 *
 * @see [TooltipController].
 */
val IdeMetaPlugin.toolTipController: ExtensionPhase
  get() = addAppService(TooltipController::class.java) {
    MetaTooltipController() // hijack TooltipController and replace it with ours
  }

/**
 * Removes default Editor mouse hover popup listeners and replaces them by custom ones. The custom ones rely on
 * [MetaTooltipRenderer] for the rendering logic. Mouse hover popup listeners are the ones showing tooltips for
 * applicable inspections, so this effectively enables us to show custom CSS for applicable inspections.
 *
 * @see [MetaTooltipRenderer].
 */
private val IdeMetaPlugin.replaceEditorMouseListeners: ExtensionPhase
  get() = addFileEditorListener(
    fileOpened = { _: FileEditorManager, _: VirtualFile, _: FileEditor, document: Document ->
      EditorFactory.getInstance().getEditors(document).mapNotNull { editor ->
        val editorMouseEP = ExtensionPointName<EditorMouseListener>("com.intellij.editorFactoryMouseListener")
        val editorMouseMotionEP = ExtensionPointName<EditorMouseMotionListener>("com.intellij.editorFactoryMouseMotionListener")

        removeExtension(editorMouseEP, "com.intellij.openapi.editor.EditorMouseHoverPopupManager\$MyEditorMouseEventListener")
        removeExtension(editorMouseMotionEP, "com.intellij.openapi.editor.EditorMouseHoverPopupManager\$MyEditorMouseMotionEventListener")

        editor.addEditorMouseListener(MetaEditorMouseHoverPopupManager.EditorMouseEventListener())
        editor.addEditorMouseMotionListener(MetaEditorMouseHoverPopupManager.EditorMouseMotionEventListener())
      }
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
