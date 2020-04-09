package arrow.meta.ide

import arrow.meta.Plugin
import arrow.meta.ide.phases.IdeContext
import arrow.meta.phases.ExtensionPhase

typealias IdePlugin = Plugin<IdeContext>

operator fun String.invoke(phases: IdeContext.() -> List<ExtensionPhase>): IdePlugin =
  Plugin(this, phases)