package arrow.meta.ide.plugins.proofs.inspections.coercions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.inspections.coercions.explicit.localExplicitCoercions
import arrow.meta.ide.plugins.proofs.inspections.coercions.implicit.localImplicitCoercion
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.coercionInspections: ExtensionPhase
  get() = Composite(
    localImplicitCoercion,
    localExplicitCoercions
  )
