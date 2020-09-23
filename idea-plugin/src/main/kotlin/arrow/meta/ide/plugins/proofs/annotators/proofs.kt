package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.annotators.coercion.explicitPropertyCoercion
import arrow.meta.ide.plugins.proofs.annotators.coercion.explicitValArgumentCoercion
import arrow.meta.ide.plugins.proofs.annotators.coercion.implicitCoercion
import arrow.meta.ide.plugins.proofs.annotators.given.givenCallSite
import arrow.meta.ide.plugins.proofs.annotators.given.givenParameter
import arrow.meta.ide.plugins.proofs.annotators.refinement.refinementCallSite
import arrow.meta.ide.plugins.proofs.annotators.resolution.incorrectAndTooManyRefinements
import arrow.meta.ide.plugins.proofs.annotators.resolution.ownershipViolations
import arrow.meta.ide.plugins.proofs.annotators.resolution.proofAmbiguities
import arrow.meta.ide.plugins.proofs.annotators.resolution.publishedInternalProofs
import arrow.meta.ide.plugins.proofs.annotators.resolution.unresolvedGivenCallSite
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase

val IdeMetaPlugin.proofAnnotators: ExtensionPhase
  get() = Composite(
    // resolution
    // TODO: add skippedProof Annotator
    addAnnotator(annotator = publishedInternalProofs),
    addAnnotator(annotator = ownershipViolations),
    addAnnotator(annotator = proofAmbiguities),
    addAnnotator(annotator = incorrectAndTooManyRefinements),
    addAnnotator(annotator = refinementCallSite),
    addAnnotator(annotator = unresolvedGivenCallSite),
    // given
    addAnnotator(annotator = givenParameter),
    addAnnotator(annotator = givenCallSite),
    // coercion
    addAnnotator(annotator = implicitCoercion),
    addAnnotator(annotator = explicitPropertyCoercion),
    addAnnotator(annotator = explicitValArgumentCoercion)
  )
