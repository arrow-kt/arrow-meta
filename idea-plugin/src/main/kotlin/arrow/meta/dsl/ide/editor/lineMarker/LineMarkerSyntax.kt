package arrow.meta.dsl.ide.editor.lineMarker

import arrow.meta.dsl.ide.utils.IdeUtils
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
import javax.swing.Icon

interface LineMarkerSyntax {

  /**
   * This technique adds an LineMarker on the specified PsiElement similar to the Recursive Kotlin Icon [org.jetbrains.kotlin.idea.highlighter.KotlinRecursiveCallLineMarkerProvider]
   * or Suspended Icon [org.jetbrains.kotlin.idea.highlighter.KotlinSuspendCallLineMarkerProvider]
   * TODO: Add more Techniques such as the one from Elm
   */
  @Suppress("UNCHECKED_CAST")
  fun <A> IdeMetaPlugin.addLineMarkerProvider(
    icon: Icon,
    onElement: PsiElement.() -> A?,
    matchOn: (element: A) -> Boolean,
    message: (element: A) -> String = Noop.string1(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
  ): ExtensionPhase =
    addLineMarkerProvider(
      matchOn as (PsiElement) -> Boolean,
      onElement,
      { psi: PsiElement ->
        lineMarkerInfo(icon, psi, message as (PsiElement) -> String, placed)
      }
    )

  fun <A> IdeMetaPlugin.addLineMarkerProvider(
    matchOn: (psi: PsiElement) -> Boolean,
    onElement: PsiElement.() -> A?,
    slowLineMarker: (psi: PsiElement) -> LineMarkerInfo<PsiElement>?,
    lineMarkerInfo: (psi: PsiElement) -> LineMarkerInfo<PsiElement>? = Noop.nullable1()
  ): ExtensionPhase =
    extensionProvider(
      LineMarkerProviders.INSTANCE,
      object : LineMarkerProvider {
        override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
          lineMarkerInfo(element)

        override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
          for (element: PsiElement in elements.filter { IdeUtils.isNotNull(onElement(it)) }) {
            ProgressManager.checkCanceled()
            if (matchOn(element))
              slowLineMarker(element)?.let { result.add(it) }
          }
        }
      }
    )

  fun LineMarkerSyntax.lineMarkerInfo(
    icon: Icon,
    element: PsiElement,
    message: (PsiElement) -> String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT
    // nav: GutterIconNavigationHandler<*>? = null TODO
  ): LineMarkerInfo<PsiElement> =
    object : LineMarkerInfo<PsiElement>(
      element,
      element.textRange,
      icon,
      message,
      null,
      placed
    ) {
      override fun createGutterRenderer(): GutterIconRenderer =
        object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
          override fun getClickAction(): AnAction? = null // to place breakpoint on mouse click
        }
    }
}
