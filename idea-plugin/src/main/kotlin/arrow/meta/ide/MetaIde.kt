package arrow.meta.ide

import arrow.meta.Plugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.internal.registry.IdeInternalRegistry
import arrow.meta.ide.phases.IdeContext
import arrow.meta.phases.ExtensionPhase

interface MetaIde : IdeSyntax, IdeInternalRegistry

typealias IdePlugin = Plugin<IdeContext>

operator fun String.invoke(phases: IdeContext.() -> List<ExtensionPhase>): IdePlugin =
  Plugin(this, phases)