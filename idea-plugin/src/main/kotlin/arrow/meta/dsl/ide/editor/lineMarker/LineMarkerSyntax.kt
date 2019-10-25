package arrow.meta.dsl.ide.editor.lineMarker

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

interface LineMarkerSyntax {

  /**
   * This technique adds an LineMarker on the specified PsiElement similar to the Recursive Kotlin Icon [org.jetbrains.kotlin.idea.highlighter.KotlinRecursiveCallLineMarkerProvider]
   * or Suspended Icon [org.jetbrains.kotlin.idea.highlighter.KotlinSuspendCallLineMarkerProvider]
   * TODO: Add more Techniques such as the one from Elm
   */
  fun <A> IdeMetaPlugin.addLineMarkerProvider(
    icon: Icon,
    message: (A) -> String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
    matchOn: (psi: PsiElement) -> A?
  ): ExtensionPhase =
    addLineMarkerProvider(
      matchOn,
      /*{ psi: A ->
        lineMarkerInfo(icon, psi, message, placed)
      }*/
    )

  fun <A> IdeMetaPlugin.addLineMarkerProvider(
    matchOn: (psi: PsiElement) -> A?,
    slowLineMarker: (psi: PsiElement) -> LineMarkerInfo<PsiElement>?,
    lineMarkerInfo: (psi: PsiElement) -> LineMarkerInfo<PsiElement>? = Noop.nullable1()
  ): ExtensionPhase =
    extensionProvider(
      LineMarkerProviders.INSTANCE,
      object : LineMarkerProvider {
        override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
          lineMarkerInfo(element)

        override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
          for (element: PsiElement in elements.filter { matchOn(it)?.run { true } ?: false }) {
            ProgressManager.checkCanceled()
            slowLineMarker(element)?.let { result.add(it) }
          }
        }
      }
    )

  @Suppress("UNCHECKED_CAST")
  fun <P : PsiElement> LineMarkerSyntax.lineMarkerInfo(
    icon: Icon,
    element: P,
    message: (P) -> String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT
    // nav: GutterIconNavigationHandler<*>? = null TODO
  ): LineMarkerInfo<P> =
    object : LineMarkerInfo<P>(
      element,
      element.textRange,
      icon,
      message,
      null,
      placed
    ) {
      override fun createGutterRenderer(): GutterIconRenderer =
        object : LineMarkerInfo.LineMarkerGutterIconRenderer<P>(this) {
          override fun getClickAction(): AnAction? = null // to place breakpoint on mouse click
        }
    }
}
