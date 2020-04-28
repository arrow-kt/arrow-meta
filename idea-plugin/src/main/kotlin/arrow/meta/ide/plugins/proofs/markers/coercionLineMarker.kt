package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.coercionCallSiteLineMarker: ExtensionPhase
  get() = Composite(
    implicitCoercionValueArgumentLineMarker,
    implicitCoercionPropertyLineMarker
  )
