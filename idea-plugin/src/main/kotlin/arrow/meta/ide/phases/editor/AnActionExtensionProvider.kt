package arrow.meta.ide.phases.editor

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.TimerListener

sealed class AnActionExtensionProvider : ExtensionPhase {
  data class RegisterAction(val actionId: String, val action: AnAction) : AnActionExtensionProvider()
  data class UnregisterAction(val actionId: String) : AnActionExtensionProvider()
  data class ReplaceAction(val actionId: String, val newAction: AnAction) : AnActionExtensionProvider()
  data class AddTimerListener(val delay: Int, val listener: TimerListener) : AnActionExtensionProvider()
  data class AddTransparentTimerListener(val delay: Int, val listener: TimerListener) : AnActionExtensionProvider()
  data class RemoveTimerListener(val listener: TimerListener) : AnActionExtensionProvider()
  data class RemoveTransparentTimerListener(val listener: TimerListener) : AnActionExtensionProvider()
}
