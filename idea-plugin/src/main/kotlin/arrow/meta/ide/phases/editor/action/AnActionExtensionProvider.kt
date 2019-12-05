package arrow.meta.ide.phases.editor.action

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.TimerListener
import arrow.meta.ide.dsl.editor.action.AnActionSyntax

/**
 * @see [AnActionSyntax]
 */
sealed class AnActionExtensionProvider : ExtensionPhase {
  /**
   * @see [AnActionSyntax.addAnAction]
   */
  data class RegisterAction(val actionId: String, val action: AnAction) : AnActionExtensionProvider()

  /**
   * @see [AnActionSyntax.unregisterAnAction]
   */
  data class UnregisterAction(val actionId: String) : AnActionExtensionProvider()

  /**
   * @see [AnActionSyntax.replaceAnAction]
   */
  data class ReplaceAction(val actionId: String, val newAction: AnAction) : AnActionExtensionProvider()

  /**
   * @see [AnActionSyntax.addTimerListener]
   */
  data class AddTimerListener(val delay: Int, val listener: TimerListener) : AnActionExtensionProvider()

  /**
   * @see [AnActionSyntax.addTransparentTimerListener]
   */
  data class AddTransparentTimerListener(val delay: Int, val listener: TimerListener) : AnActionExtensionProvider()

  /**
   * @see [AnActionSyntax.removeTimerListener]
   */
  data class RemoveTimerListener(val listener: TimerListener) : AnActionExtensionProvider()

  /**
   * @see [AnActionSyntax.removeTransparentTimerListener]
   */
  data class RemoveTransparentTimerListener(val listener: TimerListener) : AnActionExtensionProvider()
}
