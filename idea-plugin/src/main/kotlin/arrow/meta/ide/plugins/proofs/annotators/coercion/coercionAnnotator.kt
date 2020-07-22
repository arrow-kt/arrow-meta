package arrow.meta.ide.plugins.proofs.annotators.coercion

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.coercionAnnotator: ExtensionPhase
  get() = Composite(
    implicitCoercion,
    explicitPropertyCoercion,
    explicitValArgumentCoercion
  )
