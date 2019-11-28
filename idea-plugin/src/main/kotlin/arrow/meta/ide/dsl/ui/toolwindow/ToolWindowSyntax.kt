package arrow.meta.ide.dsl.ui.toolwindow

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowEP
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.Icon

interface ToolWindowSyntax {
  /**
   * This adds a persistent ToolWindow to the editor
   */
  fun IdeMetaPlugin.addToolWindow(
    id: String,
    anchor: String,
    icon: Icon
  ): ExtensionPhase =
    extensionProvider(ToolWindowEP.EP_NAME,
      object : ToolWindowEP() {
        val icon: Icon = icon
        val id: String = id
        val anchor: String = anchor

        override fun getToolWindowFactory(): ToolWindowFactory {
          return super.getToolWindowFactory()
        }

        override fun getFactoryClass(): Class<out ToolWindowFactory> {
          return super.getFactoryClass()
        }

        override fun getCondition(): Condition<Project>? {
          return super.getCondition()
        }
      },
      LoadingOrder.FIRST)

  /**
   * dynamic toolWindow
   */
  fun IdeMetaPlugin.toolWindow(f: ToolWindowManager.() -> ToolWindow)
}