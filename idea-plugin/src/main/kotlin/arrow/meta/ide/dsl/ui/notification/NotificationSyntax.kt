package arrow.meta.ide.dsl.ui.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import javax.swing.Icon

/**
 * IntelliJ defines various notification techniques.
 * [NotificationSyntax] models [Top-Level Notifications](https://twitter.com/47deg/status/1202865913051176960?s=20).
 * They're also called `non-modal notifications`.
 * Check out [Joachim's Blog-post](https://www.plugin-dev.com/intellij/general/notifications/).
 */
interface NotificationSyntax {

  fun notification(displayId: String, content: NotificationContent, type: NotificationType, icon: Icon? = null): Notification =
    object : Notification(displayId, icon, content.title, content.subTitle, content.content, type, NotificationListener.URL_OPENING_LISTENER) {
      override fun isImportant(): Boolean = content.important
    }
}

data class NotificationContent(
  val title: String,
  val content: String,
  val important: Boolean = false,
  val subTitle: String = "",
  val dropDownText: String = ""
)