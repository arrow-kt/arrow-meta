package arrow.meta.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.CandidateResolver
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallDiagnostic
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallAtom
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.calls.tower.NewResolutionOldInference
import org.jetbrains.kotlin.resolve.calls.tower.PSICallResolver
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo

interface CallResolutionInterceptor : ExtensionPhase {

  fun CompilerContext.interceptCandidates(
    candidates: Collection<NewResolutionOldInference.MyCandidate>,
    context: BasicCallResolutionContext,
    candidateResolver: CandidateResolver,
    callResolver: CallResolver,
    name: Name,
    kind: NewResolutionOldInference.ResolutionKind,
    tracing: TracingStrategy
  ): Collection<NewResolutionOldInference.MyCandidate>

  fun CompilerContext.interceptFunctionCandidates(
    candidates: Collection<FunctionDescriptor>,
    scopeTower: ImplicitScopeTower,
    resolutionContext: BasicCallResolutionContext,
    resolutionScope: ResolutionScope,
    callResolver: CallResolver,
    name: Name,
    location: LookupLocation
  ): Collection<FunctionDescriptor>

  fun CompilerContext.interceptFunctionCandidates(
    candidates: Collection<FunctionDescriptor>,
    scopeTower: ImplicitScopeTower,
    resolutionContext: BasicCallResolutionContext,
    resolutionScope: ResolutionScope,
    callResolver: PSICallResolver,
    name: Name,
    location: LookupLocation,
    dispatchReceiver: ReceiverValueWithSmartCastInfo?,
    extensionReceiver: ReceiverValueWithSmartCastInfo?
  ): Collection<FunctionDescriptor>

  fun CompilerContext.interceptVariableCandidates(
    candidates: Collection<VariableDescriptor>,
    scopeTower: ImplicitScopeTower,
    resolutionContext: BasicCallResolutionContext,
    resolutionScope: ResolutionScope,
    callResolver: CallResolver,
    name: Name,
    location: LookupLocation
  ): Collection<VariableDescriptor>

  fun CompilerContext.interceptVariableCandidates(
    candidates: Collection<VariableDescriptor>,
    scopeTower: ImplicitScopeTower,
    resolutionContext: BasicCallResolutionContext,
    resolutionScope: ResolutionScope,
    callResolver: PSICallResolver,
    name: Name,
    location: LookupLocation,
    dispatchReceiver: ReceiverValueWithSmartCastInfo?,
    extensionReceiver: ReceiverValueWithSmartCastInfo?
  ): Collection<VariableDescriptor>

}

