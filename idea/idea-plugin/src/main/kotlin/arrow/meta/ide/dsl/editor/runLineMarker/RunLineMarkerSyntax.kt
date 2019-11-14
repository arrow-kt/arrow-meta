package arrow.meta.ide.dsl.editor.runLineMarker

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.execution.lineMarker.RunLineMarkerContributor.Info
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import javax.swing.Icon

interface RunLineMarkerSyntax {

  /**
   * a free RunLineMarker based on an Action
   * Use [arrow.meta.ide.dsl.editor.action.AnActionSyntax.anAction] to construct an Action
   */
  fun <A : PsiElement> IdeMetaPlugin.toRunLineMarkerProvider(action: AnAction, transform: (PsiElement) -> A?): ExtensionPhase =
    extensionProvider(
      EP_NAME,
      object : RunLineMarkerContributor() {
        override fun getInfo(element: PsiElement): Info? =
          transform(element)?.run { Info(action.templatePresentation.icon, { psi -> getText(action, psi) }, arrayOf(action)) }
      }
    )

  fun IdeMetaPlugin.addRunLineMarkerProvider(transform: (element: PsiElement) -> Info?): ExtensionPhase =
    extensionProvider(EP_NAME, runLineMarker(transform))

  fun RunLineMarkerSyntax.runLineMarker(
    transform: (element: PsiElement) -> Info?
  ): RunLineMarkerContributor =
    object : RunLineMarkerContributor() {
      override fun getInfo(element: PsiElement): Info? = transform(element)
    }

  fun RunLineMarkerSyntax.runLineMarkerInfo(icon: Icon, actions: List<AnAction>, message: (PsiElement) -> String): Info =
    Info(icon, message, actions.toTypedArray())

}

/**
 * Revisit this in the next Release. This may change in 19.3.
 */
internal val EP_NAME: LanguageExtension<RunLineMarkerContributor>
  get() = LanguageExtension("com.intellij.runLineMarkerContributor")
