package arrow.meta.ide.plugins.proofs

import arrow.meta.CliPlugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.proofs.folding.codeFolding
import arrow.meta.ide.plugins.proofs.inspections.explicitCoercionInspection
import arrow.meta.ide.plugins.proofs.inspections.implicitCoercionInspection
import arrow.meta.ide.plugins.proofs.markers.coercionLineMarker
import arrow.meta.ide.plugins.proofs.markers.proofLineMarkers
import arrow.meta.ide.plugins.proofs.markers.refinementLineMarkers
import arrow.meta.ide.plugins.proofs.psi.isCoercionProof
import arrow.meta.ide.plugins.proofs.psi.isExtensionProof
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
import arrow.meta.ide.plugins.proofs.psi.isRefinementProof
import arrow.meta.ide.plugins.proofs.resolve.proofsKotlinCache
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import arrow.meta.invoke as cli

val IdeMetaPlugin.typeProofsIde: IdePlugin
  get() = "Type Proofs IDE" {
    meta(
      // refinementAnnotator(),
      proofLineMarkers(ArrowIcons.SUBTYPING, KtNamedFunction::isCoercionProof),
      proofLineMarkers(ArrowIcons.INTERSECTION, KtNamedFunction::isExtensionProof),
      proofLineMarkers(ArrowIcons.REFINEMENT, KtClass::isRefinementProof),
      proofLineMarkers(ArrowIcons.ICON1, KtClassOrObject::isGivenProof),
      proofLineMarkers(ArrowIcons.ICON1, KtProperty::isGivenProof),
      proofLineMarkers(ArrowIcons.ICON1, KtFunction::isGivenProof),
      refinementLineMarkers(),
      proofsKotlinCache,
      addDiagnosticSuppressorWithCtx { suppressProvenTypeMismatch(it) },
      coercionLineMarker,
      explicitCoercionInspection,
      implicitCoercionInspection,
      codeFolding
    )
  }

val IdeMetaPlugin.typeProofsCli: CliPlugin
  get() = "Type Proofs Cli Integration".cli {
    meta(
      suppressDiagnostic { suppressProvenTypeMismatch(it) }
    )
  }
