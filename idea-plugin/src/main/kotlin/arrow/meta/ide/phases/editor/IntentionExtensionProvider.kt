package arrow.meta.ide.phases.editor

import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.impl.config.IntentionActionMetaData

sealed class IntentionExtensionProvider : ExtensionPhase {
  data class RegisterIntention(val intention: IntentionAction, val category: String) : IntentionExtensionProvider()
  data class RegisterIntentionWithMetaData(val intention: IntentionAction) : IntentionExtensionProvider()
  data class UnregisterIntention(val intention: IntentionAction) : IntentionExtensionProvider()
  data class SetAvailability(val intention: IntentionAction, val enabled: Boolean) : IntentionExtensionProvider()
  data class SetAvailabilityOnActionMetaData(val intention: IntentionActionMetaData, val enabled: Boolean) : IntentionExtensionProvider()
}
