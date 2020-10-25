package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.proofs.annotators.proofAnnotators
import arrow.meta.ide.plugins.proofs.folding.codeFolding
import arrow.meta.ide.plugins.proofs.markers.proofLineMarkers
import arrow.meta.ide.plugins.proofs.resolve.proofsKotlinCache
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch

val IdeMetaPlugin.typeProofsIde: IdePlugin
  get() = "Type Proofs IDE" {
    meta(
      addDiagnosticSuppressorWithCtx { suppressProvenTypeMismatch(it) },
      addDiagnosticSuppressorWithCtx { suppressConstantExpectedTypeMismatch(it) },
      addDiagnosticSuppressorWithCtx { suppressTypeInferenceExpectedTypeMismatch(it) },
      proofsKotlinCache,
      proofLineMarkers,
      proofAnnotators,
      codeFolding
    )
  }
