package arrow.meta.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.ProofVertex
import arrow.meta.phases.resolve.`isSubtypeOf(NewKotlinTypeChecker)`
import arrow.meta.phases.resolve.asProof
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.proofCache
import arrow.meta.phases.resolve.typeArgumentsMap
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.container.ContainerConsistencyException
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.inference.InferenceErrorData
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.model.AllCandidatesResolutionResult
import org.jetbrains.kotlin.resolve.calls.model.GivenCandidate
import org.jetbrains.kotlin.resolve.calls.model.KotlinCall
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallKind
import org.jetbrains.kotlin.resolve.calls.model.ReceiverExpressionKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.ReceiverKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.TypeArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.receivers.CastImplicitClassReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath

val ArrowProof: FqName =
  FqName("arrow.Proof")

enum class ProofStrategy {
  /**
   * ```kotlin:ank:silent
   * import arrow.Proof
   * import arrow.TypeProof
   *
   * inline class PositiveInt(val value: Int)
   *
   * @Proof(of = [Subtyping])
   * fun PositiveInt.toInt(): Int = value
   *
   * @Proof(of = [Subtyping])
   * fun Int.toPositiveInt(): PositiveInt? =
   * ```
   */
  Subtyping,
  Extension,
  Refinement,
  Negation
}

data class Proof(
  val from: KotlinType,
  val to: KotlinType,
  val through: FunctionDescriptor,
  val proofType: ProofStrategy
)

data class ProofCandidate(
  val from: KotlinType,
  val to: KotlinType,
  val subType: UnwrappedType,
  val superType: UnwrappedType,
  val through: FunctionDescriptor
)

fun Proof.intersection(): KotlinType =
  from.intersection(to)

fun Proof.intersection(substitutor: NewTypeSubstitutorByConstructorMap): KotlinType =
  substitutor.safeSubstitute(from.unwrap())
    .intersection(substitutor.safeSubstitute(to.unwrap()))

fun FunctionDescriptor.isProof(): Boolean =
  annotations.hasAnnotation(ArrowProof)

fun List<Proof>.extensions(): List<Proof> =
  this//filter { it.proofType == ProofStrategy.Extension }

fun List<Proof>.extensions(types: Collection<KotlinType>): List<Proof> =
  extensions(*types.toTypedArray())

fun List<Proof>.extensions(vararg types: KotlinType): List<Proof> =
  extensions().mapNotNull { proof ->
    val include = types.any {
      !it.isTypeParameter() && proof.from.isSubtypeOf(it)
    }
    if (include) {
      proof
    } else null
  }

fun List<Proof>.subtyping(vararg types: KotlinType): List<Proof> =
  filter { it.proofType == ProofStrategy.Subtyping }
    .mapNotNull { proof ->
      val include = types.any { proof.from.`isSubtypeOf(NewKotlinTypeChecker)`(it) }
      if (include) {
        proof
      } else null
    }

fun ClassDescriptor.syntheticMemberFunction2(fn: SimpleFunctionDescriptor): SimpleFunctionDescriptor? {
  val dispatchReceiver = ReceiverParameterDescriptorImpl(this, CastImplicitClassReceiver(this, defaultType), Annotations.EMPTY)
  return fn.syntheticFunction2(this, null, dispatchReceiver, fn.source)
}

fun SimpleFunctionDescriptor.syntheticFunction2(
  containingDeclaration: DeclarationDescriptor?,
  extensionReceiver: ReceiverParameterDescriptor?,
  dispatchReceiver: ReceiverParameterDescriptor?,
  source: SourceElement
): SimpleFunctionDescriptor? {
  return SimpleFunctionDescriptorImpl.create(
    containingDeclaration ?: this.containingDeclaration,
    Annotations.EMPTY,
    name,
    CallableMemberDescriptor.Kind.DECLARATION,
    source
  ).initialize(
    extensionReceiver,
    dispatchReceiver,
    typeParameters,
    valueParameters,
    returnType,
    Modality.FINAL,
    Visibilities.PUBLIC
  ).newCopyBuilder()
    .setOriginal(this@syntheticFunction2)
    .build()
}

fun List<Proof>.lexicalScope(currentScope: LexicalScope, containingDeclaration: DeclarationDescriptor): LexicalScope {
  val types = map { it.intersection(/* TODO substitutor */) }
  return if (types.isEmpty()) currentScope
  else types.reduce { acc, kotlinType -> acc.intersection(kotlinType) }.let { proofIntersection ->
    val ownerDescriptor = AnonymousFunctionDescriptor(containingDeclaration, Annotations.EMPTY, CallableMemberDescriptor.Kind.DECLARATION, SourceElement.NO_SOURCE, false)
    val extensionReceiver = ExtensionReceiver(ownerDescriptor, proofIntersection, null)
    val extensionReceiverParamDescriptor = ReceiverParameterDescriptorImpl(ownerDescriptor, extensionReceiver, ownerDescriptor.annotations)
    ownerDescriptor.initialize(extensionReceiverParamDescriptor, null, emptyList(), emptyList(), ownerDescriptor.returnType, null, Visibilities.PUBLIC)
    LexicalScopeImpl(currentScope, ownerDescriptor, true, extensionReceiverParamDescriptor, LexicalScopeKind.FUNCTION_INNER_SCOPE)
  }
}

fun ModuleDescriptor.cachedExtensions(): List<CallableMemberDescriptor> =
  proofCache[this]?.extensionCallables ?: emptyList()

fun (() -> List<Proof>).chainedMemberScope(): MemberScope {
  val synthProofs by lazy {
    this().flatMap { proof ->
      proof.extensionCallables { true }
        .filterIsInstance<SimpleFunctionDescriptor>()
    }
  }

  return ProofsMemberScope { synthProofs }
}

fun Proof.extensionCallables(descriptorNameFilter: (Name) -> Boolean): List<CallableMemberDescriptor> =
  to.memberScope
    .getContributedDescriptors(nameFilter = descriptorNameFilter)
    .toList()
    .filterIsInstance<CallableMemberDescriptor>()
    .mapNotNull(CallableMemberDescriptor::discardAnyFakeOverrides)

private fun CallableMemberDescriptor.discardAnyFakeOverrides(): CallableMemberDescriptor? =
  when (kind) {
    CallableMemberDescriptor.Kind.FAKE_OVERRIDE ->
      if (dispatchReceiverParameter?.type == builtIns.anyType) null
      else this
    else -> this
  }

fun List<Proof>.hasProof(compilerContext: CompilerContext, subType: KotlinType, superType: KotlinType): Boolean =
  matchingCandidates(compilerContext, subType, superType).isNotEmpty()

fun List<Proof>.subtypingProof(compilerContext: CompilerContext, subType: KotlinType, superType: KotlinType): Proof? =
  filter { it.proofType == ProofStrategy.Subtyping || it.proofType == ProofStrategy.Extension }.matchingCandidates(compilerContext, subType, superType).firstOrNull()

fun FunctionDescriptor.dispatchTo(receiverValue: ReceiverValue): FunctionDescriptor =
  safeAs<SimpleFunctionDescriptor>()?.newCopyBuilder()?.setDispatchReceiverParameter(
    ReceiverParameterDescriptorImpl(
      this, receiverValue, Annotations.EMPTY
    )
  )?.build() ?: this


fun List<Proof>.matchingCandidates(
  compilerContext: CompilerContext,
  subType: KotlinType,
  superType: KotlinType
): List<Proof> =
  if (subType.isError || superType.isError) emptyList()
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
              givenCandidates = this@matchingCandidates.map {
                GivenCandidate(
                  descriptor = it.through,
                  dispatchReceiver = null,
                  knownTypeParametersResultingSubstitutor = null
                )
              },
              extensionReceiver = receiverValue
            )
            return if (callResolutionResult is AllCandidatesResolutionResult) {
              val selectedCandidates = callResolutionResult.allCandidates.filter {
                it.diagnostics.isEmpty()
              }
              val proofs = selectedCandidates.mapNotNull { it.candidate.resolvedCall.candidateDescriptor.safeAs<SimpleFunctionDescriptor>()?.asProof() }
              proofs
            } else emptyList()
          }
        } ?: emptyList<Proof>()
      } catch (e: ContainerConsistencyException) {
        emptyList<Proof>()
      }
    }
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

/*
fun List<Proof>.matchingCandidates(
  callResolver: CallResolver,
  subType: KotlinType,
  superType: KotlinType
): List<Proof> =
  filter { (from, to, proof) ->
    val candidate = ProofCandidate(
      from = from,
      to = to,
      subType = subType.unwrappedNotNullableType,
      superType = superType.unwrappedNotNullableType,
      through = proof
    )
    val appliedConversion = candidate.applyConversion()
    appliedConversion?.provesWithBaselineTypeChecker() ?: false
  }
 */

fun Graph<ProofVertex, Proof>.shortestPath(
  subType: KotlinType,
  superType: KotlinType
): GraphPath<ProofVertex, Proof>? =
  try {
    val shortestPath = DijkstraShortestPath(this)
    val fromVertex = ProofVertex(subType)
    val sink = ProofVertex(superType)
    val path = shortestPath.getPath(fromVertex, sink)
    path
  } catch (e: IllegalArgumentException) {
    null
  }


val ProofCandidate.typeSubstitutor: NewTypeSubstitutorByConstructorMap
  get() {
    val fromArgsMap = from.typeArgumentsMap(subType)
    //val toArgsMap = to.typeArgumentsMap(superType)
    val allArgsMap =
      fromArgsMap.filter { it.key.type.isTypeParameter() } + mapOf(
        through.module.builtIns.nothingType.asTypeProjection() to TypeUtils.DONT_CARE.asTypeProjection()
      )
    //toArgsMap.filter { it.key.type.isTypeParameter() } +
    val substitutor = NewTypeSubstitutorByConstructorMap(
      allArgsMap.map {
        it.key.type.constructor to it.value.type.unwrap()
      }.toMap()
    )
    return substitutor
//    val fromArgsMap = from.typeArgumentsMap(subType)
//    val toArgsMap = to.typeArgumentsMap(superType)
//    val allArgsMap =
//      fromArgsMap.filter { it.key.type.isTypeParameter() } + toArgsMap.filter { it.key.type.isTypeParameter() }
//    return NewTypeSubstitutorByConstructorMap(
//      allArgsMap.map {
//        it.key.type.constructor to it.value.type.unwrap()
//      }.toMap()
//    )
  }


fun Collection<Proof>.dump() {
  println("Arrow Type Proofs:\n${toList().joinToString("\n") { (from, to, through) ->
    "$from -> [${through.fqNameSafe}] -> $to:"
  }}")
}

fun List<Proof>.importableNames(): Set<FqName> =
  extensions()
    .flatMap { it.extensionCallables { true } }
    .map { FqName(it.fqNameSafe.asString().replace(".${it.containingDeclaration.name}.", ".")) }
    .toSet()

fun List<Proof>.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name): List<SimpleFunctionDescriptor> =
  syntheticMemberFunctions(receiverTypes).filter { it.name == name }

fun List<Proof>.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): List<SimpleFunctionDescriptor> =
  extensions(receiverTypes).flatMap { proof ->
    proof.extensionCallables { true }
      .filterIsInstance<SimpleFunctionDescriptor>()
  }

fun CompilerContext.suppressProvenTypeMismatch(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.TYPE_MISMATCH &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtExpression, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.a
      val superType = diagnosticWithParameters.b
      Log.Verbose({ "suppressProvenTypeMismatch: $subType, $superType, $this" }) {
        proofs.subtypingProof(this, subType, superType) != null
      }
    } == true

fun CompilerContext.suppressTypeInferenceExpectedTypeMismatch(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.a
      val superType = diagnosticWithParameters.b
      Log.Verbose({ "suppressTypeInferenceExpectedTypeMismatch: $subType, $superType, $this" }) {
        proofs.subtypingProof(this, subType, superType) != null
      }
    } == true

fun CompilerContext.suppressConstantExpectedTypeMismatch(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.CONSTANT_EXPECTED_TYPE_MISMATCH &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtConstantExpression, String, KotlinType>>()?.let { diagnosticWithParameters ->
      val superType = diagnosticWithParameters.b
      val elementType = diagnosticWithParameters.psiElement.elementType.toString()
      val subType = when (elementType) {
        "INTEGER_CONSTANT" -> module?.builtIns?.intType
        "CHARACTER_CONSTANT" -> module?.builtIns?.charType
        "FLOAT_CONSTANT" -> module?.builtIns?.floatType
        "BOOLEAN_CONSTANT" -> module?.builtIns?.booleanType
        "NULL" -> module?.builtIns?.nullableAnyType
        else -> null
      }
      Log.Verbose({ "suppressConstantExpectedTypeMismatch: $subType, $superType, $this" }) {
        subType?.let {
          proofs.subtypingProof(this, it, superType) != null
        }
      }
    } == true

fun CompilerContext.suppressUpperboundViolated(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.TYPE_INFERENCE_UPPER_BOUND_VIOLATED &&
    diagnostic.safeAs<DiagnosticWithParameters1<PsiElement, InferenceErrorData>>()?.let { diagnosticWithParameters ->
      val inferenceErrorData = diagnosticWithParameters.a
      true
//      Log.Verbose({ "suppressUpperboundViolated: $subType, $superType, $this" }) {
//        proofs.subtypingProof(this, subType, superType) != null
//      }
    } == true
