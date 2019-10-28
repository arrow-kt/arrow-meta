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
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

/**
 * This technique adds an LineMarker on the specified PsiElement similar to the Recursive Kotlin Icon [org.jetbrains.kotlin.idea.highlighter.KotlinRecursiveCallLineMarkerProvider]
 * or Suspended Icon [org.jetbrains.kotlin.idea.highlighter.KotlinSuspendCallLineMarkerProvider].
 * Registration Impl may change to 2019.3 EAP
 * TODO: Add more Techniques such as the one from Elm
 */
interface LineMarkerSyntax {

  /**
   * It is advised to create LineMarkerInfo for leaf elements (e.g: Psi(Identifier)) and not composite PsiElements
   * check [com.intellij.codeInsight.daemon.LineMarkerProvider]
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiElement> IdeMetaPlugin.addLineMarkerProvider(
    icon: Icon,
    transform: (PsiElement) -> A?,
    message: (element: A) -> String = Noop.string1(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
  ): ExtensionPhase =
    addLineMarkerProvider(
      transform,
      { lineMarkerInfo(icon, it, message as (PsiElement) -> String, placed) }
    )

  /**
   * This takes care of registration and allows the user to have the composite in Scope
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiNameIdentifierOwner> IdeMetaPlugin.addLineMarkerProvider(
    icon: Icon,
    transform: (PsiElement) -> A?,
    composite: Class<A>,
    message: (A) -> String = Noop.string1(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
  ): ExtensionPhase =
    addLineMarkerProvider(
      { transform(it)?.identifyingElement },
      { identifier: PsiElement ->
        PsiTreeUtil.getParentOfType(identifier, composite)?.let { psi: A ->
          lineMarkerInfo(icon, identifier, { message(psi) }, placed)
        }
      }
    )

  fun <A : PsiElement> IdeMetaPlugin.addLineMarkerProvider(
    matchOn: (PsiElement) -> A?,
    slowLineMarker: (a: A) -> LineMarkerInfo<PsiElement>?,
    lineMarkerInfo: (a: A) -> LineMarkerInfo<PsiElement>? = Noop.nullable1()
  ): ExtensionPhase =
    extensionProvider(
      LineMarkerProviders.INSTANCE,
      object : LineMarkerProvider {
        override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
          transform(element)?.let(
            lineMarkerInfo)

        override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
          for (element: PsiElement in elements.filter { IdeUtils.isNotNull(matchOn(it)) }) {
            ProgressManager.checkCanceled()
            matchOn(element)?.let { a ->
              slowLineMarker(a)?.let { result.add(it) }
            }
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
