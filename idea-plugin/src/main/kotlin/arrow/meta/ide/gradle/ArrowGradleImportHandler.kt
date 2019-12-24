package arrow.meta.ide.gradle

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.jetbrains.kotlin.idea.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.plugins.gradle.model.GradleExtensions
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData

const val titleOnNotificationsWindow = "Arrow Meta Notifications"
const val notificationTitle = "Arrow Meta"

class ArrowGradleImportHandler : GradleProjectImportHandler {

  override fun importBySourceSet(facet: KotlinFacet, sourceSetNode: DataNode<GradleSourceSetData>) {
    if (sourceSetNode.data.moduleName == "test") return

    val arrowExtensions =
      sourceSetNode.parent
        ?.children
        ?.filter { it.data is GradleExtensions }
        ?.map { it.data as GradleExtensions }
        ?.flatMap { it.extensions.filter { extension -> extension.name == "arrow" } }

    if (arrowExtensions?.size == 0) {
      val notificationContent = "Gradle Plugin is missing in ${(sourceSetNode.parent?.data as ModuleData).id}: <br /><code>plugins { id \"io.arrow-kt.arrow\" }</code>"
      val notification = Notification(titleOnNotificationsWindow, notificationTitle, notificationContent, NotificationType.INFORMATION)
      Notifications.Bus.notify(notification, facet.module.project)
    }
  }

  override fun importByModule(facet: KotlinFacet, moduleNode: DataNode<ModuleData>) {
  }
}