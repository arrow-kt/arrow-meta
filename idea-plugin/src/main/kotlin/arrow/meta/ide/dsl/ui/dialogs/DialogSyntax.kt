package arrow.meta.ide.dsl.ui.dialogs

import arrow.meta.ide.MetaIde
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.IdeView
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.actions.NewKotlinFileAction
import javax.swing.Icon

/**
 * Dialogs are isomorphic to a function `(UserInput) -> (AnAction)`.
 * Therefore Dialogs have the ability to manipulate the editor environment as extensive as `AnAction`.
 * Consequently, we can treat `Dialogs` as `AnAction` with a display.
 */
interface DialogSyntax {
  /**
   * ```kotlin:ank
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.MetaIde
   * import arrow.meta.ide.invoke
   * import org.jetbrains.kotlin.idea.KotlinFileType
   *
   * val MetaIde.createFilePlugin: IdePlugin
   *  get() = "Create File Dialog" {
   *   meta(
   *     addFileAction("ExampleAction", "New File", "Creates a new File",
   *       buildDialog = { project, directory ->
   *         setTitle("MyTitle")
   *         addKind("File", KotlinFileType.INSTANCE.icon, "Kotlin File")
   *       }
   *      )
   *     )
   *   }
   * ```
   * @param actionId has to be unique
   */
  fun MetaIde.addFileAction(
    actionId: String,
    createText: String,
    actionDescription: String,
    buildDialog: CreateFileFromTemplateDialog.Builder.(project: Project, directory: PsiDirectory) -> Unit = Noop.effect3,
    postProcess: (createdElement: PsiFile, templateName: String, customProperties: MutableMap<String, String>) -> Unit = Noop.effect3,
    fileIcon: Icon? = KotlinFileType.INSTANCE.icon,
    actionName: (directory: PsiDirectory, newName: String, templateName: String) -> String =
      { _, _, _ -> createText },
    createFileFromTemplate: (name: String, template: FileTemplate, dir: PsiDirectory) -> PsiFile? =
      { name, template, dir -> NewKotlinFileAction.createFileFromTemplateWithStat(name, template, dir) },
    startInWriteAction: Boolean = false
  ): ExtensionPhase = // TODO: Investigate system when there is no `postProcess`
    addAnAction(actionId, newFileAction(createText, actionDescription, buildDialog, postProcess, fileIcon, actionName, createFileFromTemplate, startInWriteAction))

  fun DialogSyntax.newFileAction(
    createText: String,
    actionDescription: String,
    buildDialog: CreateFileFromTemplateDialog.Builder.(project: Project, directory: PsiDirectory) -> Unit = Noop.effect3,
    postProcess: (createdElement: PsiFile, templateName: String, customProperties: MutableMap<String, String>) -> Unit = Noop.effect3,
    fileIcon: Icon? = KotlinFileType.INSTANCE.icon,
    actionName: (directory: PsiDirectory, newName: String, templateName: String) -> String =
      { _, _, _ -> createText },
    createFileFromTemplate: (name: String, template: FileTemplate, dir: PsiDirectory) -> PsiFile? =
      { name, template, dir -> NewKotlinFileAction.createFileFromTemplate(name, template, dir) },
    startInWriteAction: Boolean = false
  ): CreateFileFromTemplateAction =
    object : CreateFileFromTemplateAction(createText, actionDescription, fileIcon), DumbAware {
      override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String? =
        directory?.let { d -> templateName?.let { t -> actionName(d, newName, t) } }

      override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder): Unit =
        buildDialog(builder, project, directory)

      override fun startInWriteAction(): Boolean = startInWriteAction

      override fun isAvailable(dataContext: DataContext?): Boolean =
        dataContext?.let { ctx ->
          if (super.isAvailable(ctx))
            LangDataKeys.IDE_VIEW.getData(ctx)?.let { ideView: IdeView ->
              PlatformDataKeys.PROJECT.getData(ctx)?.let { project ->
                ideView.directories.any {
                  ProjectRootManager.getInstance(project).fileIndex.isInSourceContent(it.virtualFile)
                }
              }
            }
          else
            false
        } ?: false

      override fun postProcess(createdElement: PsiFile, templateName: String?, customProperties: MutableMap<String, String>?): Unit =
        templateName?.let { name -> customProperties?.let { prop -> postProcess(createdElement, name, prop) } }
          ?: Unit

      override fun createFileFromTemplate(name: String?, template: FileTemplate?, dir: PsiDirectory?): PsiFile? =
        name?.let { n -> template?.let { t -> dir?.let { d -> createFileFromTemplate(n, t, d) } } }
    }
}