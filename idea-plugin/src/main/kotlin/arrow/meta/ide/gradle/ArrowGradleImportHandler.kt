package arrow.meta.ide.gradle

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.jetbrains.kotlin.idea.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.plugins.gradle.model.GradleExtensions
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

const val notificationsEntry = "Arrow Meta Notifications"
const val title = "Arrow Meta"
const val arrowPlugin = """plugins { id "io.arrow-kt.arrow" }"""

class ArrowGradleImportHandler : GradleProjectImportHandler {

  override fun importBySourceSet(facet: KotlinFacet, sourceSetNode: DataNode<GradleSourceSetData>) {
    when (sourceSetNode.data.moduleName) {
      "main" ->
        if (arrowExtensions(sourceSetNode) == 0) {
          val notificationContent = createNotificationContent((sourceSetNode.parent?.data as ModuleData).id)
          val notification = Notification(notificationsEntry, title, notificationContent, NotificationType.INFORMATION)

          notification.addAction(object : NotificationAction("Copy code") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
              Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(arrowPlugin), null)
            }
          })
          Notifications.Bus.notify(notification, facet.module.project)
        }
    }
  }

  private fun arrowExtensions(sourceSetNode: DataNode<GradleSourceSetData>): Int? =
    sourceSetNode.parent
      ?.children
      ?.filter { it.data is GradleExtensions }
      ?.map { it.data as GradleExtensions }
      ?.flatMap { it.extensions.filter { extension -> extension.name == "arrow" } }
      ?.size

  private fun createNotificationContent(projectId: String): String =
    """
    Gradle Plugin is missing in $projectId project:
    <br /><br />$arrowPlugin
    """

  override fun importByModule(facet: KotlinFacet, moduleNode: DataNode<ModuleData>) {}
}