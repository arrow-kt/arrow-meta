package arrow.meta.ide.plugins.proofs

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.coerceProofLineMarker
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke

val IdeMetaPlugin.typeProofsIde: Plugin
  get() = "Type Proofs IDE" {
    meta(
//      proofLineMarkers(ArrowIcons.INTERSECTION, KtNamedFunction::isExtensionProof),
//      proofLineMarkers(ArrowIcons.NEGATION, KtNamedFunction::isNegationProof),
//      proofLineMarkers(ArrowIcons.REFINEMENT, KtNamedFunction::isRefinementProof),
//      refinementLineMarkers(),
//      addDiagnosticSuppressor { suppressProvenTypeMismatch(it) },
//      refinementAnnotator(),
//      makeExplicitCoercionIntention(ctx),
//      makeImplicitCoercionIntention(ctx),
      coerceProofLineMarker(ArrowIcons.ICON4, ctx)
    )
  }
