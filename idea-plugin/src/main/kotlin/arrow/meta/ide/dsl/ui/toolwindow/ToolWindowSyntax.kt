package arrow.meta.ide.dsl.ui.toolwindow

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.ui.ToolwindowProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import java.awt.BorderLayout
import java.awt.LayoutManager
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Tool windows have several use-cases, though there used in two different scenario. Either, to display content resulting from an computation, which [ToolWindowSyntax] materializes with
 * [addToolWindowFromAnAction] and [registerToolWindow], or establishing a 'persistently' visible tool window the user can interact with at all time. Please refer to this
 * [link](http://www.jetbrains.org/intellij/sdk/docs/user_interface_components/tool_windows.html?search=tool) for the second scenario.
 */
interface ToolWindowSyntax {

  /**
   * registers an tool window with displayName [toolId] and activates it by invoking the Action with the title [toolId].
   * The following example enables the internal `Fir Explorer`, whenever the current document is a kotlin file.
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.invoke
   * import com.intellij.openapi.actionSystem.CommonDataKeys
   * import org.jetbrains.kotlin.idea.KotlinFileType
   * import org.jetbrains.kotlin.idea.actions.internal.FirExplorerToolWindow
   *
   * val IdeMetaPlugin.exampleToolWindow: Plugin
   *   get() = "ShowFirIdePlugin" {
   *     meta(
   *       addToolWindowFromAnAction(
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
   * @param content these extensions allow you to define JComponents [toolWindowWithEditor], [toolWindowContent], [simpleWorkSpace], [toolWindowWithEditor] or create your own costume implementation.
   * @param anchor where the tool window is located at ide start
   * @param actionId needs to be unique
   */
  fun IdeMetaPlugin.addToolWindowFromAnAction(
    toolId: String,
    actionId: String,
    icon: Icon,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    update: (AnActionEvent) -> Unit = Noop.effect1
  ): ExtensionPhase =
    addAnAction(
      actionId,
      anAction(
        toolId,
        {
          it.project?.let { p ->
            ToolwindowProvider.RegisterToolWindow(toolId, icon, content, canCloseContent, anchor, isLockable, p)
              .registerOrActivate()
          }
        },
        update
      )
    )

  /**
   * registers a tool window with the [content]. Please refer to [toolWindowContent], [simpleWorkSpace], [simpleWorkSpace] or create a costume implementation.
   * @param anchor where the Toolwindow is located at ide start
   * @param
   */
  fun IdeMetaPlugin.registerToolWindow(
    id: String,
    icon: Icon,
    content: (Project, ToolWindow) -> JComponent,
    canCloseContent: Boolean = false,
    anchor: ToolWindowAnchor = ToolWindowAnchor.RIGHT,
    isLockable: Boolean = false,
    project: Project
  ): ExtensionPhase =
    ToolwindowProvider.RegisterToolWindow(id, icon, content, canCloseContent, anchor, isLockable, project)

  /**
   * constructs a [JPanel] with an [Editor] inside
   * @param dispose needs to be implemented using at least [EditorFactory.releaseEditor]
   * @param layoutManager check all SubTypes for various use-cases
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

  fun ToolWindowSyntax.simpleWorkSpace(
    vertical: Boolean,
    borderless: Boolean,
    toolbar: ActionManager.(DefaultActionGroup) -> ActionToolbar,
    register: SimpleToolWindowPanel.(ActionToolbar) -> Unit = { registerToolbar(it) },
    data: SimpleToolWindowPanel.(dataId: String) -> Any? = Noop.effect2,
    dispose: () -> Unit = Noop.effect0
  ): SimpleToolWindowPanel =
    object : SimpleToolWindowPanel(vertical, borderless), Disposable {
      override fun dispose() = dispose()
      override fun getData(dataId: String): Any? = data(this, dataId)

      init {
        register(this, toolbar(ActionManager.getInstance(), DefaultActionGroup()))
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

  /**
   * this extension is a default registration method
   */
  fun SimpleToolWindowPanel.registerToolbar(toolbar: ActionToolbar): Unit {
    toolbar.setTargetComponent(this)
    this.toolbar = toolbar.component
  }
}
