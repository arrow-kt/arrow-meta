package arrow.meta.ide.dsl.editor.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInsight.intention.impl.config.IntentionActionMetaData
import com.intellij.codeInsight.intention.impl.config.IntentionManagerSettings

interface IntentionUtilitySyntax {
  fun IntentionUtilitySyntax.availableIntentions(): List<IntentionAction> =
    IntentionManager.getInstance()?.availableIntentionActions?.toList() ?: emptyList()

  fun IntentionAction.isEnabled(): Boolean =
    IntentionManagerSettings.getInstance().isEnabled(this)

  fun IntentionActionMetaData.isEnabled(): Boolean =
    IntentionManagerSettings.getInstance().isEnabled(this)
}