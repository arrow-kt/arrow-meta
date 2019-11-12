package arrow.meta.ide.dsl.editor.action

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.action.AnActionExtensionProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.TimerListener
import com.intellij.openapi.application.ModalityState
import javax.swing.Icon

// TODO: Check Default of actionId
interface AnActionSyntax : AnActionUtilitySyntax {
  fun IdeMetaPlugin.addAnAction(
    actionId: String,
    action: AnAction
  ): ExtensionPhase =
    AnActionExtensionProvider.RegisterAction(actionId, action)

  fun IdeMetaPlugin.replaceAnAction(
    actionId: String,
    newAction: AnAction
  ): ExtensionPhase =
    AnActionExtensionProvider.ReplaceAction(actionId, newAction)

  fun IdeMetaPlugin.unregisterAnAction(
    actionId: String
  ): ExtensionPhase =
    AnActionExtensionProvider.UnregisterAction(actionId)

  fun IdeMetaPlugin.addTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    AnActionExtensionProvider.AddTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))

  fun IdeMetaPlugin.addTransparentTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    AnActionExtensionProvider.AddTransparentTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))

  fun IdeMetaPlugin.removeTransparentTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    AnActionExtensionProvider.RemoveTransparentTimerListener(listener)


  fun IdeMetaPlugin.removeTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    AnActionExtensionProvider.RemoveTimerListener(listener)

  /**
   * TODO: Add more costume attributes: ShortCuts etc.
   * [http://www.jetbrains.org/intellij/sdk/docs/tutorials/action_system/working_with_custom_actions.html
   * http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/creating_an_action.html
   * one example is [org.jetbrains.kotlin.idea.actions.DecompileKotlinToJavaAction]
   */
  fun AnActionSyntax.anAction(
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction() {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.anAction(
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(icon) {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.anAction(
    title: String,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(title) {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.anAction(
    title: String,
    description: String,
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(title, description, icon) {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.timerListener(
    modalityState: ModalityState,
    run: () -> Unit
  ): TimerListener =
    object : TimerListener {
      override fun run() = run()

      override fun getModalityState(): ModalityState = modalityState
    }
}
