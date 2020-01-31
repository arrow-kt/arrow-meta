package arrow.meta.ide.dsl.ui.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import javax.swing.Icon

/**
 * IntelliJ defines various notification techniques.
 */
interface NotificationSyntax {

  /**
   * [notification] creates one [Top-Level Notifications](https://twitter.com/47deg/status/1202865913051176960?s=20).
   * They're also called `non-modal notifications`.
   * Check out [Joachim's Blog-post](https://www.plugin-dev.com/intellij/general/notifications/).
   */
  fun NotificationSyntax.notification(displayId: String, content: NotificationContent, type: NotificationType, icon: Icon? = null): Notification =
    object : Notification(displayId, icon, content.title, content.subTitle, content.content, type, NotificationListener.URL_OPENING_LISTENER) {
      override fun isImportant(): Boolean = content.important
    }

  /**
   * There are several examples here [org.jetbrains.kotlin.idea.configuration.KotlinSetupEnvironmentNotificationProvider], [org.jetbrains.kotlin.idea.debugger.KotlinAlternativeSourceNotificationProvider]
   * or [com.intellij.ide.FileChangedNotificationProvider] and many more
   * @param key define [key] with [Key.create]
   */
  fun NotificationSyntax.editorNotificationPanel(
    key: Key<EditorNotificationPanel>,
    create: (file: VirtualFile, fileEditor: FileEditor, project: Project) -> EditorNotificationPanel?
  ): EditorNotifications.Provider<EditorNotificationPanel> =
    object : EditorNotifications.Provider<EditorNotificationPanel>() {
      override fun getKey(): Key<EditorNotificationPanel> = key

      override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project: Project): EditorNotificationPanel? =
        create(file, fileEditor, project)
    }
}

data class NotificationContent(
  val title: String,
  val content: String,
  val important: Boolean = false,
  val subTitle: String = "",
  val dropDownText: String = ""
)