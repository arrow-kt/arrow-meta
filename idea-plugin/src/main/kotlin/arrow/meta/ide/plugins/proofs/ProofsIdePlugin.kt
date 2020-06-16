package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.proofs.annotators.coercionAnnotator
import arrow.meta.ide.plugins.proofs.annotators.refinementAnnotator
import arrow.meta.ide.plugins.proofs.coercions.coercionInspections
import arrow.meta.ide.plugins.proofs.folding.codeFolding
import arrow.meta.ide.plugins.proofs.markers.proofLineMarkers
import arrow.meta.ide.plugins.proofs.markers.refinementLineMarkers
import arrow.meta.ide.plugins.proofs.psi.isCoercionProof
import arrow.meta.ide.plugins.proofs.psi.isExtensionProof
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
import arrow.meta.ide.plugins.proofs.resolve.proofsKotlinCache
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

val IdeMetaPlugin.typeProofsIde: IdePlugin
  get() = "Type Proofs IDE" {
    meta(
      proofLineMarkers(ArrowIcons.SUBTYPING, KtNamedFunction::isCoercionProof),
      proofLineMarkers(ArrowIcons.INTERSECTION, KtNamedFunction::isExtensionProof),
      proofLineMarkers(ArrowIcons.ICON1, KtClassOrObject::isGivenProof),
      proofLineMarkers(ArrowIcons.ICON1, KtProperty::isGivenProof),
      proofLineMarkers(ArrowIcons.ICON1, KtFunction::isGivenProof),
      refinementLineMarkers(),
      refinementAnnotator(),
      proofsKotlinCache,
      addDiagnosticSuppressorWithCtx { suppressProvenTypeMismatch(it) },
      // coercionCallSiteLineMarker,
      coercionAnnotator,
      // declarationDocProvider,
      coercionInspections,
      codeFolding
    )
  }
