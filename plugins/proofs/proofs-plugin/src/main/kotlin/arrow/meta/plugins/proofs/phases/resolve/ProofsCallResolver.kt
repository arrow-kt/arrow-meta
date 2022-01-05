package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.Proof
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.LambdaArgument
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.calls.components.InferenceSession
import org.jetbrains.kotlin.resolve.calls.components.KotlinCallCompleter
import org.jetbrains.kotlin.resolve.calls.components.KotlinResolutionCallbacks
import org.jetbrains.kotlin.resolve.calls.components.NewOverloadingConflictResolver
import org.jetbrains.kotlin.resolve.calls.components.candidate.ResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.context.CheckArgumentTypesMode
import org.jetbrains.kotlin.resolve.calls.model.CallResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.KotlinCall
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallComponents
import org.jetbrains.kotlin.resolve.calls.model.SimpleCandidateFactory
import org.jetbrains.kotlin.resolve.calls.model.checkCallInvariants
import org.jetbrains.kotlin.resolve.calls.model.freshReturnType
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind
import org.jetbrains.kotlin.resolve.calls.tower.CandidateWithBoundDispatchReceiver
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.calls.tower.KnownResultProcessor
import org.jetbrains.kotlin.resolve.calls.tower.PSICallResolver
import org.jetbrains.kotlin.resolve.calls.tower.TowerResolver
import org.jetbrains.kotlin.resolve.calls.tower.forceResolution
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.resolve.scopes.receivers.Receiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.expressions.ExpressionTypingContext

class ProofsCallResolver(
  private val towerResolver: TowerResolver,
  private val kotlinCallCompleter: KotlinCallCompleter,
  private val overloadingConflictResolver: NewOverloadingConflictResolver,
  private val callComponents: KotlinCallComponents,
  private val psiCallResolver: PSICallResolver
) {

  fun List<Proof>.resolveCandidates(
    scopeTower: ImplicitScopeTower,
    kotlinCall: KotlinCall,
    expectedType: UnwrappedType,
    collectAllCandidates: Boolean,
    extensionReceiver: ReceiverValueWithSmartCastInfo?
  ): CallResolutionResult {
    kotlinCall.checkCallInvariants()
    val trace = BindingTraceContext.createTraceableBindingTrace()
    val context =
      ExpressionTypingContext.newContext(
        trace,
        scopeTower.lexicalScope,
        DataFlowInfo.EMPTY,
        expectedType,
        LanguageVersionSettingsImpl.DEFAULT,
        DataFlowValueFactoryImpl(LanguageVersionSettingsImpl.DEFAULT)
      )
    val fakeCall =
      object : Call {
        override fun getCallOperationNode(): ASTNode? = null
        override fun getExplicitReceiver(): Receiver? = null
        override fun getDispatchReceiver(): ReceiverValue? = null
        override fun getCalleeExpression(): KtExpression? = null
        override fun getValueArgumentList(): KtValueArgumentList? = null
        override fun getValueArguments(): List<ValueArgument> = emptyList()
        override fun getFunctionLiteralArguments(): List<LambdaArgument> = emptyList()
        override fun getTypeArguments(): List<KtTypeProjection> = emptyList()
        override fun getTypeArgumentList(): KtTypeArgumentList? = null
        override fun getCallElement(): KtElement =
          throw IllegalStateException("this is a fake call element")
        override fun getCallType(): Call.CallType = Call.CallType.DEFAULT
      }
    val basicCallContext =
      BasicCallResolutionContext.create(
        context,
        fakeCall,
        CheckArgumentTypesMode.CHECK_VALUE_ARGUMENTS
      )
    val resolutionCallbacks =
      psiCallResolver.createResolutionCallbacks(trace, InferenceSession.default, basicCallContext)
    val candidateFactory =
      SimpleCandidateFactory(
        callComponents,
        scopeTower,
        kotlinCall,
        resolutionCallbacks
      )

    val resolutionCandidates = map {
      it.fold(
          given = { givenCandidate(candidateFactory) },
        )
        .forceResolution()
    }

    if (collectAllCandidates) {
      val allCandidates =
        towerResolver.runWithEmptyTowerData(
          KnownResultProcessor(resolutionCandidates),
          TowerResolver.AllCandidatesCollector(),
          useOrder = false
        )
      return kotlinCallCompleter.createAllCandidatesResult(
        allCandidates,
        expectedType,
        resolutionCallbacks
      )
    }
    val candidates =
      towerResolver.runWithEmptyTowerData(
        KnownResultProcessor(resolutionCandidates),
        TowerResolver.SuccessfulResultCollector(),
        useOrder = true
      )
    return choseMostSpecific(candidateFactory, resolutionCallbacks, expectedType, candidates)
  }

  private fun GivenProof.givenCandidate(
    candidateFactory: SimpleCandidateFactory
  ): ResolutionCandidate =
    candidateFactory.createCandidate(
      towerCandidate = CandidateWithBoundDispatchReceiver(null, callableDescriptor, emptyList()),
      explicitReceiverKind = ExplicitReceiverKind.NO_EXPLICIT_RECEIVER,
      extensionReceiver = null
    )

  private fun choseMostSpecific(
    candidateFactory: SimpleCandidateFactory,
    resolutionCallbacks: KotlinResolutionCallbacks,
    expectedType: UnwrappedType?,
    candidates: Collection<ResolutionCandidate>
  ): CallResolutionResult {
    var refinedCandidates =
      candidates.filter {
        it.resolvedCall.freshReturnType?.let { a ->
          expectedType?.let { b -> baseLineTypeChecker.isSubtypeOf(a, b) }
        }
          ?: false
      }
    if (!callComponents.languageVersionSettings.supportsFeature(
        LanguageFeature.RefinedSamAdaptersPriority
      )
    ) {
      val nonSynthesized = candidates.filter { !it.resolvedCall.candidateDescriptor.isSynthesized }
      if (nonSynthesized.isNotEmpty()) {
        refinedCandidates = nonSynthesized
      }
    }

    val maximallySpecificCandidates =
      overloadingConflictResolver.chooseMaximallySpecificCandidates(
        refinedCandidates,
        CheckArgumentTypesMode.CHECK_VALUE_ARGUMENTS,
        discriminateGenerics = true
      )

    return kotlinCallCompleter.runCompletion(
      candidateFactory,
      maximallySpecificCandidates,
      expectedType,
      resolutionCallbacks
    )
  }
}
