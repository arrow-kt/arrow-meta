package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.proofs.annotators.refinementAnnotator
import arrow.meta.ide.plugins.proofs.coercions.coercionInspections
import arrow.meta.ide.plugins.proofs.folding.codeFolding
import arrow.meta.ide.plugins.proofs.markers.coercionCallSiteLineMarker
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
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.typeProofsIde: IdePlugin
  get() = "Type Proofs IDE" {
    meta(
      proofLineMarkers(ArrowIcons.SUBTYPING) { it.safeAs<KtNamedFunction>()?.takeIf { it.isCoercionProof() } },
      proofLineMarkers(ArrowIcons.INTERSECTION) { it.safeAs<KtNamedFunction>()?.takeIf { it.isExtensionProof() } },
      proofLineMarkers(ArrowIcons.ICON1) { it.safeAs<KtClassOrObject>()?.takeIf { it.isGivenProof() } },
      proofLineMarkers(ArrowIcons.ICON1) { it.safeAs<KtProperty>()?.takeIf { it.isGivenProof() } },
      proofLineMarkers(ArrowIcons.ICON1) { it.safeAs<KtFunction>()?.takeIf { it.isGivenProof() } },
      refinementLineMarkers(),
      refinementAnnotator(),
      proofsKotlinCache,
      addDiagnosticSuppressorWithCtx { suppressProvenTypeMismatch(it) },
      coercionCallSiteLineMarker,
      coercionInspections,
      codeFolding
    )

  }
