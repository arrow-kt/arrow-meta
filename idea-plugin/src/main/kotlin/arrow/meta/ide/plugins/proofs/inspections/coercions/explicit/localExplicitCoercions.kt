package arrow.meta.ide.plugins.proofs.inspections.coercions.explicit

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.localExplicitCoercions: ExtensionPhase
  get() = Composite(
    localExplicitCoercionOnKtProperty,
    localExplicitCoercionOnKtValArg
  )
