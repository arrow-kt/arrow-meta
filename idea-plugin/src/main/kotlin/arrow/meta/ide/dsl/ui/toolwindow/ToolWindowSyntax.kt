package arrow.meta.ide.dsl.ui.toolwindow

import arrow.meta.ide.MetaIde
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import arrow.meta.ide.phases.ui.ToolwindowProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import java.awt.BorderLayout
import java.awt.LayoutManager
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.HyperlinkEvent

/**
 * Tool windows have several use-cases, though there used in two different scenario. Either, to display content resulting from an computation, which [ToolWindowSyntax] materializes with
 * [addToolWindowWithAction] and [registerToolWindow], or establishing a 'persistently' visible tool window the user can interact with at all time. Please refer to this
 * [link](http://www.jetbrains.org/intellij/sdk/docs/user_interface_components/tool_windows.html?search=tool) for the second scenario.
 */
interface ToolWindowSyntax {

  /**
   * registers an Action with the title [toolId] and [actionId].
   * When the user executes the latter a tool window with displayName [toolId] is activated or registered by absence.
   * The following example enables the internal `Fir Explorer`, whenever the current document is a kotlin file.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.MetaIde
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.ide.invoke
   * import com.intellij.openapi.actionSystem.CommonDataKeys
   * import org.jetbrains.kotlin.idea.KotlinFileType
   * import org.jetbrains.kotlin.idea.actions.internal.FirExplorerToolWindow
   *
   * val MetaIde.exampleToolWindow: IdePlugin
   *   get() = "ShowFirInTheIde" {
   *     meta(
   *       addToolWindowFromAction(
   *         toolId = "Show Fir",
   *         actionId = "Unique",
   *         icon = ArrowIcons.ICON4,
   *         content = { project, toolWindow ->
   *           FirExplorerToolWindow(project, toolWindow)
   *         },
   *         update = { e ->
   *           e.presentation.isEnabled = e.project != null && e.getData(CommonDataKeys.PSI_FILE)?.fileType == KotlinFileType.INSTANCE
   *         }
   *       )
   *     )
   *   }
   * ```
   * @param content these extensions allow you to define JComponents [toolWindowWithEditor], [toolWindowContent] or create your own implementation.
   * @param anchor sets the tool window on its initially position
   * @param actionId needs to be unique
   * @see toolWindowAction Tool windows can be composed with LineMarkers in `clickAction` [LineMarkerSyntax.addLineMarkerProvider]
   */
  fun MetaIde.addToolWindowFromAction(
    toolId: String,
    actionId: String,
    icon: Icon,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): ExtensionPhase =
    addAnAction(actionId, toolWindowAction(toolId, icon, content, canCloseContent, anchor, isLockable, update))

  fun MetaIde.toolWindowAction(
    toolId: String,
    icon: Icon,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): AnAction =
    anAction(
      toolId,
      {
        it.project?.let { p ->
          ToolwindowProvider.RegisterToolWindow(toolId, icon, content, canCloseContent, anchor, isLockable, p)
            .registerOrActivate()
        }
      },
      update = update
    )

  /**
   * registers a tool window with the [content].
   * @param content Please refer to [toolWindowWithEditor], [toolWindowContent] or create a costum implementation.
   * @param anchor sets where the tool window is initially located
   * @see addToolWindowFromAction
   */
  fun MetaIde.registerToolWindow(
    toolId: String,
    icon: Icon,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    project: Project
  ): ExtensionPhase =
    ToolwindowProvider.RegisterToolWindow(toolId, icon, content, canCloseContent, anchor, isLockable, project)

  /**
   * unregisters a tool window with its [toolId]
   */
  fun MetaIde.unregisterToolWindow(toolId: String, project: Project): ExtensionPhase =
    ToolwindowProvider.UnRegisterToolWindow(toolId, project)

  /**
   * Adds a notification balloon to the Toolwindow and only disappears if the users clicks on it.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.MetaIde
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.ide.invoke
   * import com.intellij.openapi.ui.MessageType
   * import com.intellij.openapi.wm.ToolWindowId
   *
   * val MetaIde.toolWindowBalloons: IdePlugin
   *   get() = "ToolWindowBalloon" {
   *     meta(
   *       addToolWindowNotification(
   *         ToolWindowId.PROJECT_VIEW,
   *         "Unique",
   *         MessageType.INFO,
   *         "Teach your users about this tool window",
   *         ArrowIcons.ICON2
   *       )
   *     )
   *   }
   * ```
   * @see toolWindowNotification Tool windows can be composed with LineMarkers in `clickAction` [LineMarkerSyntax.addLineMarkerProvider]
   */
  fun MetaIde.addToolWindowNotification(
    toolId: String,
    actionId: String,
    type: MessageType,
    html: String,
    icon: Icon? = null,
    listener: (HyperlinkEvent) -> Unit = Noop.effect1,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): ExtensionPhase =
    addAnAction(actionId, toolWindowNotification(toolId, type, html, icon, listener, update))

  fun MetaIde.toolWindowNotification(
    toolId: String,
    type: MessageType,
    html: String,
    icon: Icon? = null,
    listener: (HyperlinkEvent) -> Unit = Noop.effect1,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): AnAction =
    anAction(
      toolId,
      {
        it.project?.let { p ->
          ToolwindowProvider.Notification(toolId, type, html, icon, listener, p).register()
        }
      },
      update = update
    )

  /**
   * this extension is a composition of [addToolWindowFromAction] and [toolWindowNotification]
   * @see toolWindowWithNotification Tool windows can be composed with LineMarkers in `clickAction` [LineMarkerSyntax.addLineMarkerProvider]
   */
  fun MetaIde.addToolWindowWithNotification(
    toolId: String,
    actionId: String,
    icon: Icon,
    type: MessageType,
    html: String,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    listener: (HyperlinkEvent) -> Unit = Noop.effect1,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): ExtensionPhase =
    addAnAction(actionId, toolWindowWithNotification(toolId, icon, type, html, content, canCloseContent, anchor, isLockable, listener, update))

  fun MetaIde.toolWindowWithNotification(
    toolId: String,
    icon: Icon,
    type: MessageType,
    html: String,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    listener: (HyperlinkEvent) -> Unit = Noop.effect1,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): AnAction =
    anAction(
      toolId,
      {
        it.project?.let { p ->
          ToolwindowProvider.RegisterToolWindow(toolId, icon, content, canCloseContent, anchor, isLockable, p)
            .registerOrActivate()
          ToolwindowProvider.Notification(toolId, type, html, icon, listener, p).register()
        }
      },
      update = update
    )

  fun MetaIde.toolWindowNotification(
    toolId: String,
    type: MessageType,
    html: String,
    project: Project,
    icon: Icon? = null,
    listener: (HyperlinkEvent) -> Unit = Noop.effect1
  ): ExtensionPhase =
    ToolwindowProvider.Notification(toolId, type, html, icon, listener, project)

  /**
   * constructs a [JPanel] with an [Editor] inside.
   *
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.MetaIde
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.ide.invoke
   * import com.intellij.openapi.application.ApplicationManager
   * import com.intellij.openapi.editor.Editor
   * import com.intellij.openapi.wm.ToolWindowAnchor
   * import org.jetbrains.kotlin.idea.KotlinFileType

   * val MetaIde.editorToolwindow: IdePlugin
   *   get() = "TestEditor in Toolwindow" {
   *     meta(
   *       addToolWindowFromAction(
   *         "TestEditor",
   *         "Unique",
   *         ArrowIcons.ICON4,
   *         content = { project, toolWindow ->
   *           toolWindowWithEditor(
   *             project = project,
   *             fileType = KotlinFileType.INSTANCE,
   *             readOnly = false,
   *             text = "val hello = 'Hello World'",
   *             register = { editor, _ ->
   *               add(editor.component) // register the editor to the displayed root content
   *               editor.appendText("// Add more code, execute tasks or register more UI components to the editor")
   *             })
   *         },
   *         anchor = ToolWindowAnchor.BOTTOM
   *       )
   *     )
   *   }
   *
   * fun Editor.appendText(text: String): Unit =
   *   ApplicationManager.getApplication().runReadAction { document.setText(document.text + text) }
   * ```
   *
   * @param dispose needs to be implemented using at least [EditorFactory.releaseEditor], which is the default implementation.
   * @param layoutManager check all SubTypes for various use-cases.
   * @see addToolWindowFromAction
   */
  fun ToolWindowSyntax.toolWindowWithEditor(
    project: Project,
    fileType: LanguageFileType,
    readOnly: Boolean,
    text: String,
    register: JPanel.(Editor, Project) -> Unit =
      { it, _ -> add(it.component) },
    layoutManager: LayoutManager = BorderLayout(),
    editor: Editor = editor(project, fileType, readOnly, text),
    dispose: EditorFactory.(Editor) -> Unit = { releaseEditor(it) }
  ): JPanel =
    object : JPanel(layoutManager), Disposable {
      override fun dispose(): Unit = dispose(EditorFactory.getInstance(), editor)

      init {
        register(this, editor, project)
      }
    }

  fun ToolWindowSyntax.toolWindowContent(
    project: Project,
    register: JPanel.(Project) -> Unit,
    layoutManager: LayoutManager = BorderLayout(),
    dispose: () -> Unit = Noop.effect0
  ): JPanel =
    object : JPanel(layoutManager), Disposable {
      override fun dispose(): Unit = dispose()

      init {
        register(this, project)
      }
    }

  /**
   * creates an Editor for the specified [fileType] including a Document with [text].
   * @param readOnly true if the editor is read-only
   */
  fun ToolWindowSyntax.editor(project: Project, fileType: LanguageFileType, readOnly: Boolean, text: String): Editor =
    EditorFactory.getInstance().run { createEditor(createDocument(text), project, fileType, readOnly) }

  fun ToolWindowSyntax.toolWindowIds(project: Project): List<String> =
    ToolWindowManager.getInstance(project).toolWindowIds.toList()
}
