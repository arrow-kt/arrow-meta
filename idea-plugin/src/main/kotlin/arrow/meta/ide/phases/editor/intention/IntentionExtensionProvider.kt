package arrow.meta.ide.phases.editor.intention

import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.impl.config.IntentionActionMetaData
import arrow.meta.ide.dsl.editor.intention.IntentionSyntax

/**
 * @see [IntentionSyntax]
 */
sealed class IntentionExtensionProvider : ExtensionPhase {
  /**
   * @see [IntentionSyntax.addIntention]
   */
  data class RegisterIntention(val intention: IntentionAction) : IntentionExtensionProvider()

  /**
   * @see [IntentionSyntax.addIntentionWithMetaData]
   */
  data class RegisterIntentionWithMetaData(val intention: IntentionAction, val category: String) : IntentionExtensionProvider()

  /**
   * @see [IntentionSyntax.unregisterIntention]
   */
  data class UnregisterIntention(val intention: IntentionAction) : IntentionExtensionProvider()

  /**
   * @see [IntentionSyntax.setIntentionAsEnabled]
   */
  data class SetAvailability(val intention: IntentionAction, val enabled: Boolean) : IntentionExtensionProvider()

  /**
   * @see [IntentionSyntax.setIntentionAsEnabled] for [IntentionActionMetaData]
   */
  data class SetAvailabilityOnActionMetaData(val intention: IntentionActionMetaData, val enabled: Boolean) : IntentionExtensionProvider()
}
