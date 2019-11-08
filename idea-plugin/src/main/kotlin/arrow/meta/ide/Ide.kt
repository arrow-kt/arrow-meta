package arrow.meta.ide

import arrow.meta.phases.ExtensionPhase

data class IdePlugin(
  val name: String,
  val meta: () -> List<ExtensionPhase>
)

operator fun String.invoke(phases: () -> List<ExtensionPhase>): IdePlugin =
  IdePlugin(this, phases)