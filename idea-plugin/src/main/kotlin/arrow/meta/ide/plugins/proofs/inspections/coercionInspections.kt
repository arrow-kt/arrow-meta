package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.coercionInspections: ExtensionPhase
  get() = Composite(
    explicitCoercionInspection,
    implicitCoercionInspection
  )