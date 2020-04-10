package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.ProofStrategy
import arrow.meta.plugins.proofs.phases.quotes.refinementExpression
import arrow.meta.plugins.proofs.phases.resolve.scopes.ProofsScopeTower
import arrow.meta.quotes.classorobject.ObjectDeclaration
import org.jetbrains.kotlin.container.ContainerConsistencyException
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.annotations.argumentValue
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
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.kotlin.types.typeUtil.replaceArgumentsWithStarProjections
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun List<ExtensionProof>.matchingCandidates(
  compilerContext: CompilerContext,
  subType: KotlinType,
  superType: KotlinType
): List<ExtensionProof> {
  val proofs = if (containsErrorsOrNothing(subType, superType)) emptyList()
  else {
    compilerContext.run {
      try {
        module?.run {
          componentProvider?.get<ProofsCallResolver>()?.let { proofsCallResolver ->
            val proofs = this@matchingCandidates.resolveProofs(subType, superType, compilerContext, proofsCallResolver, this)
            proofs
          }
        } ?: emptyList()
      } catch (e: ContainerConsistencyException) {
        emptyList<ExtensionProof>()
      }
    }
  }
  return proofs
}

private fun List<ExtensionProof>.resolveProofs(
  subType: KotlinType,
  superType: KotlinType,
  compilerContext: CompilerContext,
  proofsCallResolver: ProofsCallResolver,
  moduleDescriptor: ModuleDescriptor
): List<ExtensionProof> {
  val extensionReceiver = ProofReceiverValue(subType)
  val receiverValue = ReceiverValueWithSmartCastInfo(extensionReceiver, emptySet(), true)
  val scopeTower = ProofsScopeTower(moduleDescriptor, this, compilerContext)
  val kotlinCall: KotlinCall = receiverValue.kotlinCall()
  val callResolutionResult = proofsCallResolver.resolveGivenCandidates(
    scopeTower = scopeTower,
    kotlinCall = kotlinCall,
    expectedType = superType.unwrap(),
    collectAllCandidates = true,
    givenCandidates = givenCandidates(),
    extensionReceiver = receiverValue
  )
  return callResolutionResult.matchingProofs(subType, superType)
}

private fun containsErrorsOrNothing(vararg types: KotlinType) =
  types.any { it.isError || it.isNothing() }

private fun CallResolutionResult.matchingProofs(subType: KotlinType, superType: KotlinType): List<ExtensionProof> =
  if (this is AllCandidatesResolutionResult) {
    val selectedCandidates = allCandidates.filter {
      it.diagnostics.isEmpty()
    }
    val proofs = selectedCandidates.mapNotNull { it.candidate.resolvedCall.candidateDescriptor.safeAs<SimpleFunctionDescriptor>()?.asProof() }
      .filter { includeInCandidates(it.from, it.to, subType, superType) }
    proofs
  } else emptyList()

private fun includeInCandidates(from: KotlinType, to: KotlinType, subType: KotlinType, superType: KotlinType): Boolean =
  ((from.isTypeParameter() || baseLineTypeChecker.isSubtypeOf(from.replaceArgumentsWithStarProjections(), subType.replaceArgumentsWithStarProjections()))
    &&
    (to.isTypeParameter() || baseLineTypeChecker.isSubtypeOf(to.replaceArgumentsWithStarProjections(), superType.replaceArgumentsWithStarProjections())))

private fun List<ExtensionProof>.givenCandidates(): List<GivenCandidate> =
  map {
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

class ProofReceiverValue(private val kotlinType: KotlinType) : ReceiverValue {
  override fun replaceType(p0: KotlinType): ReceiverValue =
    ProofReceiverValue(p0)

  override fun getOriginal(): ReceiverValue = this

  override fun getType(): KotlinType = kotlinType
}

fun FunctionDescriptor.asProof(): ExtensionProof? =
  extensionReceiverParameter?.type?.let { from ->
    returnType?.let { to ->
      val annotationArgs = annotations.first().allValueArguments
      val value: ConstantValue<*>? = annotationArgs[Name.identifier("of")]
      val coerce: ConstantValue<*>? = annotationArgs[Name.identifier("coerce")]
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
        ExtensionProof(
          from = from,
          to = to,
          through = this,
          coerce = coerce?.value.safeAs() ?: false
        )
      }
    }
  }

fun List<Proof>.refinementsFor(superType: KotlinType): List<Proof> =
  filter { proof ->
    baseLineTypeChecker.isSubtypeOf(superType.makeNotNullable(), proof.to)
  }

fun List<Proof>.refinementExpressionFromAnnotation(superType: KotlinType): String? =
  refinementsFor(superType)
    .mapNotNull {
      val refinedAnnotation = (it.to.makeNotNullable().constructor.declarationDescriptor as? ClassDescriptor)?.companionObjectDescriptor?.annotations?.findAnnotation(FqName("arrow.Refinement"))
      if (refinedAnnotation != null) {
        refinedAnnotation.argumentValue("predicate")?.value as? String
      } else null
    }.firstOrNull()

fun List<Proof>.refinementExpressionFromPsi(superType: KotlinType): String? =
  refinementsFor(superType)
    .mapNotNull {
      val psi = (it.to.makeNotNullable().constructor.declarationDescriptor as? ClassDescriptor)?.companionObjectDescriptor?.findPsi()
      val ktObject = psi as? KtObjectDeclaration
      val objectDeclaration = ktObject?.let(::ObjectDeclaration)
      objectDeclaration?.refinementExpression()
    }.firstOrNull()


