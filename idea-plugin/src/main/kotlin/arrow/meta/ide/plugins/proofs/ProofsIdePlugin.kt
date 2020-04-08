package arrow.meta.ide.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.proofs.annotators.refinementAnnotator
import arrow.meta.ide.plugins.proofs.lifecycle.proofsLifecycle
import arrow.meta.ide.plugins.proofs.folding.codeFoldingOnKinds
import arrow.meta.ide.plugins.proofs.folding.codeFoldingOnTuples
import arrow.meta.ide.plugins.proofs.folding.codeFoldingOnUnions
import arrow.meta.ide.plugins.proofs.markers.coerceProofLineMarker
import arrow.meta.ide.plugins.proofs.markers.proofLineMarkers
import arrow.meta.ide.plugins.proofs.markers.refinementLineMarkers
import arrow.meta.ide.plugins.proofs.psi.isExtensionProof
import arrow.meta.ide.plugins.proofs.psi.isNegationProof
import arrow.meta.ide.plugins.proofs.psi.isRefinementProof
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import org.jetbrains.kotlin.psi.KtNamedFunction
import arrow.meta.invoke as cli

val IdeMetaPlugin.typeProofsIde: IdePlugin
  get() = "Type Proofs IDE" {
    meta(
      proofLineMarkers(ArrowIcons.INTERSECTION, KtNamedFunction::isExtensionProof),
      proofLineMarkers(ArrowIcons.NEGATION, KtNamedFunction::isNegationProof),
      proofLineMarkers(ArrowIcons.REFINEMENT, KtNamedFunction::isRefinementProof),
      refinementLineMarkers(),
      refinementAnnotator(),
      proofsLifecycle,
      //makeExplicitCoercionIntention(ctx),
      //makeImplicitCoercionIntention(ctx),
      codeFoldingOnUnions,
      codeFoldingOnTuples,
      codeFoldingOnKinds
    )
  }

val IdeMetaPlugin.typeProofsCli: CliPlugin
  get() = "Type Proofs Cli Integration".cli {
    meta(
      coerceProofLineMarker(ArrowIcons.ICON4, ctx),
      addDiagnosticSuppressor { suppressProvenTypeMismatch(it) }
    )
  }
