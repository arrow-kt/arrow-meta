package arrow.meta.ide.phases.ui

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.event.HyperlinkEvent

sealed class ToolwindowProvider : ExtensionPhase {
  data class RegisterToolWindow(
    val id: String,
    val icon: Icon,
    val content: (Project, ToolWindow) -> JComponent,
    val canCloseContent: Boolean,
    val anchor: ToolWindowAnchor,
    val isLockable: Boolean,
    val project: Project
  ) : ToolwindowProvider()

  data class UnRegisterToolWindow(val id: String, val project: Project) : ToolwindowProvider()

  data class NotificationBalloon(
    val id: String,
    val type: MessageType,
    val html: String,
    val icon: Icon?,
    val listener: (HyperlinkEvent) -> Unit,
    val project: Project
  ) : ToolwindowProvider()
}