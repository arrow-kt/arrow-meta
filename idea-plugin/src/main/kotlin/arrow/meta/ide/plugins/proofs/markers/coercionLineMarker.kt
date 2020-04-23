package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.coercionLineMarker: ExtensionPhase
  get() = Composite(
    implicitCoercionValueArgumentLineMarker,
    implicitCoercionPropertyLineMarker
  )
