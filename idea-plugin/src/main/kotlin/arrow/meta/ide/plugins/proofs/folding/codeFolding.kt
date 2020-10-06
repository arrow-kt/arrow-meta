package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.codeFolding: ExtensionPhase
  get() = Composite(
    codeFoldingOnUnions,
    codeFoldingOnTuples
    // codeFoldingOnKinds, // temporary disabled to avoid confusion due to issues
    // foldingCaretListener
  )
