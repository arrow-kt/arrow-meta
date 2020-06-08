package arrow.meta.ide.phases.additional

import arrow.meta.ide.internal.registry.IdeInternalRegistry
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.application.Application

/**
 * this interface is for plugins depending on Meta that define extensions that are
 * not in the Meta DSL for the Ide.
 * It allows one to register those extensions and make them available through the Meta DSL,
 * as if the extension is part of it.
 * The data type of those extensions should be a subtype of [AdditionalIdePhase],
 * so that overriding [AdditionalRegistry] allows one to register the aforementioned extension in [register].
 */
interface AdditionalRegistry {
  fun IdeInternalRegistry.register(app: Application, phase: AdditionalIdePhase)

  companion object : AdditionalRegistry {
    override fun IdeInternalRegistry.register(app: Application, phase: AdditionalIdePhase): Unit =
      Unit
  }
}

interface AdditionalIdePhase : ExtensionPhase