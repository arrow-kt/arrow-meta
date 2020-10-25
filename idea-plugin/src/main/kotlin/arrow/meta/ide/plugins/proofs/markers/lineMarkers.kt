package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.psi.isCoercionProof
import arrow.meta.ide.plugins.proofs.psi.isExtensionProof
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

val IdeMetaPlugin.proofLineMarkers: ExtensionPhase
  get() = Composite(
    proofLineMarkers(ArrowIcons.COERCION_ICON, KtNamedFunction::isCoercionProof),
    proofLineMarkers(ArrowIcons.EXTENSION_ICON, KtNamedFunction::isExtensionProof),
    proofLineMarkers(ArrowIcons.GIVEN_ICON, KtClassOrObject::isGivenProof),
    proofLineMarkers(ArrowIcons.GIVEN_ICON, KtProperty::isGivenProof),
    proofLineMarkers(ArrowIcons.GIVEN_ICON, KtFunction::isGivenProof),
    refinedClassLineMarker(),
    predicateLineMarker()
  )