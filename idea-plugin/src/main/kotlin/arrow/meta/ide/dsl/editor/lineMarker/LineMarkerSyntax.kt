package arrow.meta.ide.dsl.editor.lineMarker

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.IdeUtils
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
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
  fun IdeMetaPlugin.addLineMarkerProvider(
    icon: Icon,
    transform: (element: PsiElement) -> PsiElement?,
    message: (element: PsiElement) -> String = Noop.string1(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
  ): ExtensionPhase =
    lineMarkerProvider(
      transform,
      { lineMarkerInfo(icon, it, message, placed) }
    )
}

/**
 * This takes care of registration and allows the user to have the composite in Scope
 */
inline fun <reified A : PsiNameIdentifierOwner> IdeMetaPlugin.addLineMarkerProvider(
  icon: Icon,
  noinline transform: (A) -> A?,
  noinline message: (A) -> String = Noop.string1(),
  placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
): ExtensionPhase =
  lineMarkerProvider(
    { it.safeAs<A>()?.let(transform)?.identifyingElement },
    { identifier: PsiElement ->
      PsiTreeUtil.getParentOfType(identifier, A::class.java)?.let { psi: A ->
        lineMarkerInfo(icon, identifier, { message(psi) }, placed)
      }
    }
  )

inline fun <reified A : PsiElement> IdeMetaPlugin.lineMarkerProvider(
  noinline transform: (A) -> A?,
  noinline lineMarkerInfo: (a: A) -> LineMarkerInfo<A>?,
  noinline slowLineMarker: (a: A) -> LineMarkerInfo<PsiElement>? = Noop.nullable1()
): ExtensionPhase =
  extensionProvider(
    LineMarkerProviders.INSTANCE,
    object : LineMarkerProvider {
      override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<A>? =
        element.safeAs<A>()?.let(transform)?.let(lineMarkerInfo)

      override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        for (element: A in elements.mapNotNull { it.safeAs<A>() }.filter { IdeUtils.isNotNull(transform(it)) }) {
          ProgressManager.checkCanceled()
          transform(element)?.let { a ->
            slowLineMarker(a)?.let { result.add(it) }
          }
        }
      }
    }
  )

inline fun <reified A : PsiElement> LineMarkerSyntax.lineMarkerInfo(
  icon: Icon,
  element: A,
  noinline message: (A) -> String,
  placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT
  // nav: GutterIconNavigationHandler<*>? = null TODO
): LineMarkerInfo<A> =
  object : LineMarkerInfo<A>(element, element.textRange, icon, message, null, placed) {
    override fun createGutterRenderer(): GutterIconRenderer =
      object : LineMarkerInfo.LineMarkerGutterIconRenderer<A>(this) {
        override fun getClickAction(): AnAction? = null // to place breakpoint on mouse click
      }
  }
