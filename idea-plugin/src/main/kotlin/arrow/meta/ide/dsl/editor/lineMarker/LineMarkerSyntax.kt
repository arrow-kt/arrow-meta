package arrow.meta.ide.dsl.editor.lineMarker

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.IdeUtils
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import java.awt.event.MouseEvent
import javax.swing.Icon

/**
 * LineMarker's serve as visuals, which appear on specified PsiElements.
 * There are several methods to subscribe LineMarkers, the one [LineMarkerSyntax] provides is derived from Kotlin's
 * [org.jetbrains.kotlin.idea.highlighter.KotlinSuspendCallLineMarkerProvider] and [org.jetbrains.kotlin.idea.highlighter.KotlinRecursiveCallLineMarkerProvider].
 * In general, subscription techniques differ mainly in performance.
 */
interface LineMarkerSyntax {
  // TODO: Registration Impl may change to 2019.3 EAP
  // TODO: Add more Techniques such as the one from Elm

  /**
   * Due tu performance reason's it is advised that [A] is a leaf element (e.g: Psi(Identifier)) and not composite PsiElements such as [KtClass].
   * The identifying PsiElement of the latter is the class name. The PsiViewer Plugin may help to verify that [A] is a leaf element, by observing the tree structure of the PsiElement.
   * Nonetheless, IntelliJ will automatically send warnings during the `runIde` gradle task, if an implementation doesn't comply with this premise.
   * @see [com.intellij.codeInsight.daemon.LineMarkerProvider] for more information
   * @sample [arrow.meta.ide.plugins.optics.opticsIdePlugin]
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
   * [addLineMarkerProvider] is a convenience extension, which registers the Leaf element of a composite PsiElement [A] e.g.: `KtClass`
   * and circumvents effort's to find the right PsiElement.
   * In addition, plugin developer's can compose sophisticated messages, as the whole scope of [A] can be exploited.
   * @param composite In Contrast, lineMarkers constructed without this parameter have a clearly constrained message.
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
    transform: (PsiElement) -> A?,
    lineMarkerInfo: (a: A) -> LineMarkerInfo<PsiElement>?,
    slowLineMarker: (a: A) -> LineMarkerInfo<PsiElement>? = Noop.nullable1()
  ): ExtensionPhase =
    extensionProvider(
      LineMarkerProviders.INSTANCE,
      object : LineMarkerProvider {
        override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
          transform(element)?.let(
            lineMarkerInfo)

        override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
          for (element: PsiElement in elements.filter { IdeUtils.isNotNull(transform(it)) }) {
            ProgressManager.checkCanceled()
            transform(element)?.let { a ->
              slowLineMarker(a)?.let { result.add(it) }
            }
          }
        }
      }
    )

  /**
   * @param clickAction if null this will place a breakpoint on a mouse click otherwise it executes the action
   * @param isDumbAware specifies whether this LineMarkerInfo is available during index updates
   */
  fun LineMarkerSyntax.lineMarkerInfo( // TODO:
    icon: Icon,
    element: PsiElement,
    message: (PsiElement) -> String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT,
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    clickAction: AnAction? = null,
    isDumbAware: Boolean = true
  ): LineMarkerInfo<PsiElement> =
    object : LineMarkerInfo<PsiElement>(element, element.textRange, icon, message, null, placed) {
      override fun getNavigationHandler(): GutterIconNavigationHandler<PsiElement>? =
        GutterIconNavigationHandler { e, elt ->
          navigate(e, elt)
        }

      override fun createGutterRenderer(): GutterIconRenderer =
        object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
          override fun getClickAction(): AnAction? = clickAction
          override fun isDumbAware(): Boolean = isDumbAware

        }
    }
}
