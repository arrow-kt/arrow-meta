package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.plugins.proofs.phases.CoercionProof
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.RefinementProof
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.calls.components.CallableReferenceResolver
import org.jetbrains.kotlin.resolve.calls.components.InferenceSession
import org.jetbrains.kotlin.resolve.calls.components.KotlinCallCompleter
import org.jetbrains.kotlin.resolve.calls.components.KotlinResolutionCallbacks
import org.jetbrains.kotlin.resolve.calls.components.NewOverloadingConflictResolver
import org.jetbrains.kotlin.resolve.calls.context.CheckArgumentTypesMode
import org.jetbrains.kotlin.resolve.calls.model.CallResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.GivenCandidate
import org.jetbrains.kotlin.resolve.calls.model.KotlinCall
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallComponents
import org.jetbrains.kotlin.resolve.calls.model.KotlinResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.model.SimpleCandidateFactory
import org.jetbrains.kotlin.resolve.calls.model.checkCallInvariants
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind
import org.jetbrains.kotlin.resolve.calls.tower.CandidateWithBoundDispatchReceiver
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.calls.tower.KnownResultProcessor
import org.jetbrains.kotlin.resolve.calls.tower.PSICallResolver
import org.jetbrains.kotlin.resolve.calls.tower.TowerResolver
import org.jetbrains.kotlin.resolve.calls.tower.forceResolution
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.UnwrappedType

class ProofsCallResolver(
  private val towerResolver: TowerResolver,
  private val kotlinCallCompleter: KotlinCallCompleter,
  private val overloadingConflictResolver: NewOverloadingConflictResolver,
  private val callableReferenceResolver: CallableReferenceResolver,
  private val callComponents: KotlinCallComponents,
  private val psiCallResolver: PSICallResolver
) {

  fun List<Proof>.resolveCandidates(
    scopeTower: ImplicitScopeTower,
    kotlinCall: KotlinCall,
    expectedType: UnwrappedType?,
    collectAllCandidates: Boolean,
    extensionReceiver: ReceiverValueWithSmartCastInfo?
  ): CallResolutionResult {
    kotlinCall.checkCallInvariants()
    val trace = BindingTraceContext.createTraceableBindingTrace()
    val resolutionCallbacks = psiCallResolver.createResolutionCallbacks(trace, InferenceSession.default, null)
    val candidateFactory = SimpleCandidateFactory(callComponents, scopeTower, kotlinCall, resolutionCallbacks, callableReferenceResolver)

    val resolutionCandidates = map {
      it.fold(
        given = { givenCandidate(candidateFactory) },
        coercion = { extensionCandidate(candidateFactory, extensionReceiver) },
        projection = { extensionCandidate(candidateFactory, extensionReceiver) },
        refinement = { refinementCandidate(candidateFactory) }
      ).forceResolution()
    }

    if (collectAllCandidates) {
      val allCandidates = towerResolver.runWithEmptyTowerData(
        KnownResultProcessor(resolutionCandidates),
        TowerResolver.AllCandidatesCollector(),
        useOrder = false
      )
      return kotlinCallCompleter.createAllCandidatesResult(allCandidates, expectedType, resolutionCallbacks)

    }
    val candidates = towerResolver.runWithEmptyTowerData(
      KnownResultProcessor(resolutionCandidates),
      TowerResolver.SuccessfulResultCollector(),
      useOrder = true
    )
    return choseMostSpecific(candidateFactory, resolutionCallbacks, expectedType, candidates)
  }

  private fun RefinementProof.refinementCandidate(candidateFactory: SimpleCandidateFactory): KotlinResolutionCandidate =
    candidateFactory.createCandidate(
      towerCandidate = CandidateWithBoundDispatchReceiver(null, through, emptyList()),
      explicitReceiverKind = ExplicitReceiverKind.NO_EXPLICIT_RECEIVER,
      extensionReceiver = null
    )

  private fun GivenProof.givenCandidate(candidateFactory: SimpleCandidateFactory): KotlinResolutionCandidate =
    candidateFactory.createCandidate(
      towerCandidate = CandidateWithBoundDispatchReceiver(null, through, emptyList()),
      explicitReceiverKind = ExplicitReceiverKind.NO_EXPLICIT_RECEIVER,
      extensionReceiver = null
    )

  private fun ExtensionProof.extensionCandidate(candidateFactory: SimpleCandidateFactory, extensionReceiver: ReceiverValueWithSmartCastInfo?): KotlinResolutionCandidate =
    candidateFactory.createCandidate(
      towerCandidate = CandidateWithBoundDispatchReceiver(null, through, emptyList()),
      explicitReceiverKind = if (extensionReceiver != null) ExplicitReceiverKind.EXTENSION_RECEIVER else ExplicitReceiverKind.NO_EXPLICIT_RECEIVER,
      extensionReceiver = extensionReceiver
    )

  private fun choseMostSpecific(
    candidateFactory: SimpleCandidateFactory,
    resolutionCallbacks: KotlinResolutionCallbacks,
    expectedType: UnwrappedType?,
    candidates: Collection<KotlinResolutionCandidate>
  ): CallResolutionResult {
    var refinedCandidates = candidates
    if (!callComponents.languageVersionSettings.supportsFeature(LanguageFeature.RefinedSamAdaptersPriority)) {
      val nonSynthesized = candidates.filter { !it.resolvedCall.candidateDescriptor.isSynthesized }
      if (nonSynthesized.isNotEmpty()) {
        refinedCandidates = nonSynthesized
      }
    }

    val maximallySpecificCandidates = overloadingConflictResolver.chooseMaximallySpecificCandidates(
      refinedCandidates,
      CheckArgumentTypesMode.CHECK_VALUE_ARGUMENTS,
      discriminateGenerics = true
    )

    return kotlinCallCompleter.runCompletion(candidateFactory, maximallySpecificCandidates, expectedType, resolutionCallbacks)
  }
}


