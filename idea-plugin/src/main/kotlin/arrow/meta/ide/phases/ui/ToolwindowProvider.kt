package arrow.meta.ide.phases.ui

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import javax.swing.Icon
import javax.swing.JComponent

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
}