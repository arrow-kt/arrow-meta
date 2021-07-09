package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.asProof
import arrow.meta.plugins.proofs.phases.resolve.scopes.ProofsScopeTower
import org.jetbrains.kotlin.container.ContainerConsistencyException
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.model.AllCandidatesResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.CallResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.KotlinCall
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallKind
import org.jetbrains.kotlin.resolve.calls.model.ReceiverExpressionKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.ReceiverKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.TypeArgument
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.replaceArgumentsWithStarProjections

fun List<GivenProof>.matchingCandidates(
  compilerContext: CompilerContext,
  superType: KotlinType
): List<GivenProof> {
  val proofs = if (containsErrorsOrNothing(superType)) emptyList<GivenProof>()
  else {
    compilerContext.run {
      try {
        module?.run {
          componentProvider?.get<ProofsCallResolver>()?.let { proofsCallResolver ->
            val proofs = this@matchingCandidates.resolveGivenProofs(superType, compilerContext, proofsCallResolver, this)
            proofs
          }
        } ?: emptyList()
      } catch (e: ContainerConsistencyException) {
        emptyList<GivenProof>()
      }
    }
  }
  return proofs
}

fun List<GivenProof>.resolveGivenProofs(
  superType: KotlinType,
  compilerContext: CompilerContext,
  proofsCallResolver: ProofsCallResolver,
  moduleDescriptor: ModuleDescriptor
): List<GivenProof> {
  val scopeTower = ProofsScopeTower(moduleDescriptor, this, compilerContext)
  val kotlinCall: KotlinCall = kotlinCall()
  val callResolutionResult =
    proofsCallResolver.run {
      resolveCandidates(
        scopeTower = scopeTower,
        kotlinCall = kotlinCall,
        expectedType = superType.unwrap(),
        collectAllCandidates = true,
        extensionReceiver = null
      )
    }
  return callResolutionResult.matchingGivenProofs(superType)
}

fun containsErrorsOrNothing(vararg types: KotlinType) =
  types.any { it.isError || it.isNothing() }

inline fun <reified A : GivenProof> CallResolutionResult.matchingGivenProofs(superType: KotlinType): List<A> =
  if (this is AllCandidatesResolutionResult) {
    val selectedCandidates = allCandidates.filter { it.diagnostics.isEmpty() }
    val proofs = selectedCandidates.flatMap {
      it.candidate.resolvedCall.candidateDescriptor.asProof().asIterable()
    }.filter {
      it is A && includeInCandidates(it.to, superType)
    }.filterIsInstance<A>()
    proofs.toList()
  } else emptyList()

fun includeInCandidates(from: KotlinType, to: KotlinType, subType: KotlinType, superType: KotlinType): Boolean =
  includeInCandidates(from, subType) && includeInCandidates(to, superType)

fun includeInCandidates(a: KotlinType, b: KotlinType): Boolean =
  (a.isTypeParameter() || baseLineTypeChecker.isSubtypeOf(a.replaceArgumentsWithStarProjections(), b.replaceArgumentsWithStarProjections()))

fun kotlinCall(): KotlinCall =
  object : KotlinCall {
    override val argumentsInParenthesis: List<KotlinCallArgument> = emptyList()
    override val callKind: KotlinCallKind = KotlinCallKind.FUNCTION
    override val explicitReceiver: ReceiverKotlinCallArgument? = null
    override val externalArgument: KotlinCallArgument? = null
    override val isForImplicitInvoke: Boolean = false
    override val name: Name = Name.identifier("Proof type-checking and resolution")
    override val typeArguments: List<TypeArgument> = emptyList()
    override val dispatchReceiverForInvokeExtension: ReceiverKotlinCallArgument? = null
  }

fun ReceiverValueWithSmartCastInfo.kotlinCall(): KotlinCall =
  object : KotlinCall {
    override val argumentsInParenthesis: List<KotlinCallArgument> = emptyList()
    override val callKind: KotlinCallKind = KotlinCallKind.FUNCTION
    override val explicitReceiver: ReceiverKotlinCallArgument? = ReceiverExpressionKotlinCallArgument(this@kotlinCall)
    override val externalArgument: KotlinCallArgument? = null
    override val isForImplicitInvoke: Boolean = false
    override val name: Name = Name.identifier("Proof type-checking and resolution")
    override val typeArguments: List<TypeArgument> = emptyList()
    override val dispatchReceiverForInvokeExtension: ReceiverKotlinCallArgument? = null
  }

