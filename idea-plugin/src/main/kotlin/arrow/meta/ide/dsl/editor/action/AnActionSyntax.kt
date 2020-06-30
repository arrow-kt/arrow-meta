package arrow.meta.ide.dsl.editor.action

import arrow.meta.ide.MetaIde
import arrow.meta.ide.phases.editor.action.AnActionExtensionProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.impl.actions.BaseFoldingHandler
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.TimerListener
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import javax.swing.Icon

/**
 * [AnActionExtensionProvider] is in charge of the lifecycle of [AnAction].
 * [AnAction] can be placed, executed and composed into any workflow, throughout the whole application lifecycle.
 * Whether on-demand by the user or as a standalone workflow.
 * They're able to access almost every part of the ide.
 * In other words, [AnAction] contains a computation which can be evaluated multiple times at any time.
 * When a user triggers [AnAction] the [AnAction.actionPerformed] function is executed.
 * [AnAction] may have starkly different purposes, from changing CompilerConfigurations in the editor to opening UI element's with media content.
 * Here are a few Links to the Action System from IntelliJ's `Development Guide`:
 * - [Plugin Actions](http://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_actions.html?search=act)
 * - [Executing and updating actions](http://www.jetbrains.org/intellij/sdk/docs/basics/action_system.html?search=act#executing-and-updating-actions)
 */
interface AnActionSyntax : AnActionUtilitySyntax {

  /**
   * Registers the [action] with [actionId] as its identifier.
   * The [actionId] is solely used internally.
   * ```kotlin:ank:playground
   * import com.intellij.openapi.wm.ToolWindowManager
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.ide.invoke
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.MetaIde
   * import com.intellij.openapi.project.Project
   * import com.intellij.openapi.actionSystem.AnActionEvent
   *
   * val MetaIde.exampleAction: IdePlugin
   * get() = "Example Action" {
   *   meta(
   *   //sampleStart
   *     addAnAction(
   *      actionId = "Unique",
   *      action = anAction(
   *       title = "MetaAction",
   *       actionPerformed = { e: AnActionEvent ->
   *        // e.project?.let { project: Project -> ToolWindowManager.getInstance(project)?.getToolWindow("MetaToolWindow")?.activate(null) }
   *        }
   *       )
   *      )
   *   //sampleEnd
   *    )
   *  }
   * ```
   *
   * `MetaAction` is registered with the id `Unique` and opens a tool window with a registered Id `MetaToolWindow`, assuming this ToolWindowId is registered.
   * User's are able to search this Action with its title: `MetaAction`.
   * @param actionId needs to be unique
   * @param action can be composed with various [anAction] implementations
   */
  fun MetaIde.addAnAction(
    actionId: String,
    action: AnAction
  ): ExtensionPhase =
    AnActionExtensionProvider.RegisterAction(actionId, action)

  /**
   * replaces [AnAction] with [actionId] to a [newAction]
   */
  fun MetaIde.replaceAnAction(
    actionId: String,
    newAction: AnAction
  ): ExtensionPhase =
    AnActionExtensionProvider.ReplaceAction(actionId, newAction)

  /**
   * unregisters [AnAction]
   */
  fun MetaIde.unregisterAnAction(
    actionId: String
  ): ExtensionPhase =
    AnActionExtensionProvider.UnregisterAction(actionId)

  /**
   * registers a [TimerListener]
   */
  fun MetaIde.addTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    AnActionExtensionProvider.AddTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))

  /**
   * registers a transparent [TimerListener]
   */
  fun MetaIde.addTransparentTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    AnActionExtensionProvider.AddTransparentTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))

  /**
   * removes a transparent [TimerListener]
   */
  fun MetaIde.removeTransparentTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    AnActionExtensionProvider.RemoveTransparentTimerListener(listener)

  /**
   * removes a [TimerListener]
   */
  fun MetaIde.removeTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    AnActionExtensionProvider.RemoveTimerListener(listener)

  /**
   * @see org.jetbrains.kotlin.idea.actions.DecompileKotlinToJavaAction
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
    object : AnAction() { // TODO: Add more costume attributes: ShortCuts etc.
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean): Unit =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
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
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean): Unit =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
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
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
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
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.timerListener(
    modalityState: ModalityState,
    run: () -> Unit
  ): TimerListener =
    object : TimerListener {
      override fun run(): Unit = run()
      override fun getModalityState(): ModalityState = modalityState
    }

  fun AnActionSyntax.editorAction(
    handler: EditorActionHandler
  ): EditorAction =
    object : EditorAction(handler) {}

  fun AnActionSyntax.baseFoldingHandler(
    execute: (editor: Editor, caret: Caret?, ctx: DataContext?) -> Unit
  ): BaseFoldingHandler =
    object : BaseFoldingHandler() {
      override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?): Unit =
        execute(editor, caret, dataContext)
    }

  /**
   * registers a [BaseFoldingHandler]
   */
  fun MetaIde.addBaseFoldingHandler(
    actionId: String,
    execute: (editor: Editor, caret: Caret?, ctx: DataContext?) -> Unit
  ): ExtensionPhase =
    addAnAction(actionId, editorAction(baseFoldingHandler(execute)))
}
