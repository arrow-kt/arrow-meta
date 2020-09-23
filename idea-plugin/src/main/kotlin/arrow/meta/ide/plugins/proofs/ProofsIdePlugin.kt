package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.proofs.annotators.coercion.coercionAnnotator
import arrow.meta.ide.plugins.proofs.annotators.givenAnnotator
import arrow.meta.ide.plugins.proofs.annotators.proofAnnotators
import arrow.meta.ide.plugins.proofs.annotators.refinementAnnotator
import arrow.meta.ide.plugins.proofs.folding.codeFolding
import arrow.meta.ide.plugins.proofs.markers.proofLineMarkers
import arrow.meta.ide.plugins.proofs.markers.refinementLineMarkers
import arrow.meta.ide.plugins.proofs.psi.isCoercionProof
import arrow.meta.ide.plugins.proofs.psi.isExtensionProof
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
import arrow.meta.ide.plugins.proofs.resolve.proofsKotlinCache
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressConstantExpectedTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressProvenTypeMismatch
import arrow.meta.plugins.proofs.phases.resolve.diagnostics.suppressTypeInferenceExpectedTypeMismatch
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

val IdeMetaPlugin.typeProofsIde: IdePlugin
  get() = "Type Proofs IDE" {
    meta(
      addDiagnosticSuppressorWithCtx { suppressProvenTypeMismatch(it) },
      addDiagnosticSuppressorWithCtx { suppressConstantExpectedTypeMismatch(it) },
      addDiagnosticSuppressorWithCtx { suppressTypeInferenceExpectedTypeMismatch(it) },
      proofLineMarkers(ArrowIcons.COERCION_ICON, KtNamedFunction::isCoercionProof),
      proofLineMarkers(ArrowIcons.EXTENSION_ICON, KtNamedFunction::isExtensionProof),
      proofLineMarkers(ArrowIcons.GIVEN_ICON, KtClassOrObject::isGivenProof),
      proofLineMarkers(ArrowIcons.GIVEN_ICON, KtProperty::isGivenProof),
      proofLineMarkers(ArrowIcons.GIVEN_ICON, KtFunction::isGivenProof),
      refinementLineMarkers(),
      proofsKotlinCache,
      proofAnnotators,
      codeFolding
    )
  }
