package arrow.meta.ide.gradle

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.fileEditor.FileEditorManager
import org.jetbrains.kotlin.idea.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.core.util.toVirtualFile
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.plugins.gradle.model.GradleExtensions
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.nio.file.Paths
import java.util.*

const val notificationsEntry = "Arrow Meta Notifications"
const val title = "Arrow Meta"

class ArrowGradleImportHandler : GradleProjectImportHandler {

  override fun importBySourceSet(facet: KotlinFacet, sourceSetNode: DataNode<GradleSourceSetData>) {
    when (sourceSetNode.data.moduleName) {
      "main" ->
        if (arrowExtensions(sourceSetNode) == 0) {
          val properties = Properties()
          properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
          val arrowPlugin = arrowPluginContent(properties.getProperty("IDEA_PLUGIN_VERSION"))
          val projectName = (sourceSetNode.parent?.data as ModuleData).moduleName
          val notificationContent = notificationContent(projectName, arrowPlugin)
          val notification = Notification(notificationsEntry, title, notificationContent, NotificationType.INFORMATION)

          notification.addAction(object : NotificationAction("Copy code") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) { copyToClipboard(arrowPlugin) }
          })
          notification.addAction(object : NotificationAction("Open file") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) { openGradleFile(facet) }
          })
          Notifications.Bus.notify(notification, facet.module.project)
        }
    }
  }

  private fun copyToClipboard(text: String): Unit =
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)

  private fun arrowExtensions(sourceSetNode: DataNode<GradleSourceSetData>): Int? =
    sourceSetNode.parent
      ?.children
      ?.filter { it.data is GradleExtensions }
      ?.map { it.data as GradleExtensions }
      ?.flatMap { it.extensions.filter { extension -> extension.name == "arrow" } }
      ?.size

  private fun arrowPluginContent(version: String): String =
    """
    plugins {
      id "io.arrow-kt.arrow" version "$version"
    }
    """

  private fun notificationContent(projectId: String, code: String): String =
    """
    Gradle Plugin is missing in "$projectId" project:
    <br /><br />
    $code
    """

  private fun openGradleFile(facet: KotlinFacet): Unit {
    val fileEditorManager = FileEditorManager.getInstance(facet.module.project)
    val splitModuleName = facet.module.name.split(".")
    val gradleConfPath = Paths.get(
      facet.module.project.basePath,
      *splitModuleName.subList(1, splitModuleName.size - 1).toTypedArray(),
      "build.gradle"
    )
    gradleConfPath.toFile().toVirtualFile()?.let { fileEditorManager.openFile(it, true) }
  }

  override fun importByModule(facet: KotlinFacet, moduleNode: DataNode<ModuleData>) {}
}