package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.ProofStrategy
import org.jetbrains.kotlin.container.ContainerConsistencyException
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.model.AllCandidatesResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.CallResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.GivenCandidate
import org.jetbrains.kotlin.resolve.calls.model.KotlinCall
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallKind
import org.jetbrains.kotlin.resolve.calls.model.ReceiverExpressionKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.ReceiverKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.TypeArgument
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.EnumValue
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun List<Proof>.matchingCandidates(
  compilerContext: CompilerContext,
  subType: KotlinType,
  superType: KotlinType
): List<Proof> =
  if (containsErrorsOrNothing(subType, superType)) emptyList()
  else {
    compilerContext.run {
      try {
        module?.run {
          componentProvider?.get<ProofsCallResolver>()?.let { proofsCallResolver ->
            val extensionReceiver = ProofReceiverValue(subType)
            val receiverValue = ReceiverValueWithSmartCastInfo(extensionReceiver, emptySet(), true)
            val scopeTower = ProofsScopeTower(this, this@matchingCandidates)
            val kotlinCall: KotlinCall = receiverValue.kotlinCall()
            val callResolutionResult = proofsCallResolver.resolveGivenCandidates(
              scopeTower = scopeTower,
              kotlinCall = kotlinCall,
              expectedType = superType.unwrap(),
              collectAllCandidates = true,
              givenCandidates = givenCandidates(),
              extensionReceiver = receiverValue
            )
            return callResolutionResult.matchingProofs()
          }
        } ?: emptyList<Proof>()
      } catch (e: ContainerConsistencyException) {
        emptyList<Proof>()
      }
    }
  }

private fun containsErrorsOrNothing(vararg types: KotlinType) =
  types.any { it.isError || it.isNothing() }

private fun CallResolutionResult.matchingProofs(): List<Proof> =
  if (this is AllCandidatesResolutionResult) {
    val selectedCandidates = allCandidates.filter {
      it.diagnostics.isEmpty()
    }
    val proofs = selectedCandidates.mapNotNull { it.candidate.resolvedCall.candidateDescriptor.safeAs<SimpleFunctionDescriptor>()?.asProof() }
    proofs
  } else emptyList()

private fun List<Proof>.givenCandidates(): List<GivenCandidate> =
  this.map {
    GivenCandidate(
      descriptor = it.through,
      dispatchReceiver = null,
      knownTypeParametersResultingSubstitutor = null
    )
  }

private fun ReceiverValueWithSmartCastInfo.kotlinCall(): KotlinCall =
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

data class ProofCandidate(
  val from: KotlinType,
  val to: KotlinType,
  val subType: UnwrappedType,
  val superType: UnwrappedType,
  val through: FunctionDescriptor
)

private class ProofReceiverValue(private val kotlinType: KotlinType) : ReceiverValue {
  override fun replaceType(p0: KotlinType): ReceiverValue =
    ProofReceiverValue(p0)

  override fun getOriginal(): ReceiverValue = this

  override fun getType(): KotlinType = kotlinType
}

fun FunctionDescriptor.asProof(): Proof? =
  extensionReceiverParameter?.type?.let { from ->
    returnType?.let { to ->
      val annotationArgs = annotations.first().allValueArguments
      val value: ConstantValue<*>? = annotationArgs[Name.identifier("of")]
      val proofStrategy = when (value) {
        is EnumValue -> {
          val name = value.enumEntryName.asString()
          when {
            ProofStrategy.Extension.name == name -> ProofStrategy.Extension
            ProofStrategy.Refinement.name == name -> ProofStrategy.Refinement
            ProofStrategy.Negation.name == name -> ProofStrategy.Negation
            else -> null
          }
        }
        else -> null
      }
      proofStrategy?.let {
        Proof(from, to, this, it)
      }
    }
  }

