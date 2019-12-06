package arrow.meta.ide.dsl.ui.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.ui.popup.Balloon
import javax.swing.Icon

/**
 * IntelliJ defines various notification techniques.
 * [NotificationSyntax] models [Top-Level Notifications](https://twitter.com/47deg/status/1202865913051176960?s=20).
 * There also called `non-modal notifications`.
 * This Syntax is highly inspired by [Joachim's Blog-post](https://www.plugin-dev.com/intellij/general/notifications/).
 */
interface NotificationSyntax {

  fun notification( // from NotificationConfig
    displayId: String,
    content: NotificationContent,
    type: NotificationType,
    icon: Icon? = null,
    collapseDirection: Notification.CollapseActionsDirection? = null, // check it out
    fullContent: Boolean,
   //  actions: List<AnAction> = emptyList(), `addAction` is (List<AnAction>) -> Notification
    // actionIcons: Boolean, // not needed
    ctxAction: AnAction? = null
  ): Notification =
    object : Notification(displayId, icon, content.title, content.subTitle, content.content, type, NotificationListener.URL_OPENING_LISTENER) {
      override fun isImportant(): Boolean = content.important

      override fun setDropDownText(dropDownText: String): Notification = super.setDropDownText(content.dropDownText)
      override fun setContextHelpAction(action: AnAction?): Notification {
        return super.setContextHelpAction(action)
      }

      override fun setBalloon(balloon: Balloon) {
        super.setBalloon(balloon)
      }

      override fun setCollapseActionsDirection(collapseActionsDirection: CollapseActionsDirection?): Unit =
        collapseDirection?.run { super.setCollapseActionsDirection(this) }
          ?: super.setCollapseActionsDirection(collapseActionsDirection)

    }
}

data class NotificationContent(
  val title: String,
  val content: String,
  val important: Boolean = false,
  val subTitle: String = "",
  val dropDownText: String = ""
)