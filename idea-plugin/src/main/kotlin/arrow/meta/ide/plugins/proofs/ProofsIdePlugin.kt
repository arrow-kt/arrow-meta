package arrow.meta.ide.plugins.proofs

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.proofLineMarkers
import arrow.meta.ide.plugins.proofs.psi.isExtensionProof
import arrow.meta.ide.plugins.proofs.psi.isNegationProof
import arrow.meta.ide.plugins.proofs.psi.isRefinementProof
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import org.jetbrains.kotlin.psi.KtNamedFunction

val IdeMetaPlugin.proofsIdePlugin: Plugin
  get() = "Type Proofs IDE" {
    meta(
      proofLineMarkers(ArrowIcons.INTERSECTION, KtNamedFunction::isExtensionProof),
      proofLineMarkers(ArrowIcons.NEGATION, KtNamedFunction::isNegationProof),
      proofLineMarkers(ArrowIcons.REFINEMENT, KtNamedFunction::isRefinementProof),
      addDiagnosticSuppressor { suppressProvenTypeMismatch(it, module.proofs) }
    )
  }


