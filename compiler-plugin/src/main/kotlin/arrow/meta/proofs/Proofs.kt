package arrow.meta.proofs

import arrow.meta.phases.resolve.ProofVertex
import arrow.meta.phases.resolve.`isSubtypeOf(NewKotlinTypeChecker)`
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.provesWithBaselineTypeChecker
import arrow.meta.phases.resolve.typeArgumentsMap
import arrow.meta.phases.resolve.unwrappedNotNullableType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory2
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.scopes.receivers.CastImplicitClassReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeApproximator
import org.jetbrains.kotlin.types.TypeApproximatorConfiguration
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeSubstitution
import org.jetbrains.kotlin.types.TypeSubstitutor
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
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
  filter { it.proofType == ProofStrategy.Extension }

fun List<Proof>.extensions(types: Collection<KotlinType>): List<Proof> =
  extensions(*types.toTypedArray())

fun List<Proof>.extensions(vararg types: KotlinType): List<Proof> =
  filter { it.proofType == ProofStrategy.Extension }
    .mapNotNull { proof ->
      val include = types.any { proof.from.`isSubtypeOf(NewKotlinTypeChecker)`(it) }
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

fun ClassDescriptor.syntheticMemberFunction(fn: SimpleFunctionDescriptor): SimpleFunctionDescriptor? {
  val dispatchReceiver = ReceiverParameterDescriptorImpl(this, CastImplicitClassReceiver(this, defaultType), Annotations.EMPTY)
  return fn.syntheticFunction(this, null, dispatchReceiver, fn.source)
}

fun SimpleFunctionDescriptor.syntheticFunction(
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
    .setOriginal(this@syntheticFunction)
    .build()
}

fun Proof.extensionCallables(descriptorNameFilter: (Name) -> Boolean): List<CallableMemberDescriptor> =
  if (proofType == ProofStrategy.Extension) {
    to.memberScope
      .getContributedDescriptors(nameFilter = descriptorNameFilter)
      .toList()
      .filterIsInstance<CallableMemberDescriptor>()
      .mapNotNull { fn ->
        when (fn) {
          is FunctionDescriptor -> {
            when {
              fn.kind == CallableMemberDescriptor.Kind.FAKE_OVERRIDE -> null
              fn is SimpleFunctionDescriptorImpl -> {
                fn.syntheticFunction(null, fn.extensionReceiverParameter, fn.dispatchReceiverParameter, fn.original.source)
              }
              else -> fn
            }
          }
          else -> fn
        }
      }
  } else emptyList()

fun List<Proof>.hasProof(subType: KotlinType, superType: KotlinType): Boolean =
  matchingCandidates(subType, superType).isNotEmpty()

fun List<Proof>.subtypingProof(subType: KotlinType, superType: KotlinType): Proof? =
  matchingCandidates(subType, superType).firstOrNull { it.proofType == ProofStrategy.Subtyping }

fun List<Proof>.matchingCandidates(
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
    return NewTypeSubstitutorByConstructorMap(
      allArgsMap.map {
        it.key.type.constructor to it.value.type.unwrap()
      }.toMap()
    )
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

fun CallableDescriptor.substituteAndApproximateCapturedTypes2(
  substitutor: NewTypeSubstitutorByConstructorMap,
  typeApproximator: TypeApproximator
): CallableDescriptor {
  val wrappedSubstitution = object : TypeSubstitution() {
    override fun get(key: KotlinType): TypeProjection? {
      return key.unwrap().substitutedType()?.asTypeProjection()
    }

    override fun prepareTopLevelType(topLevelType: KotlinType, position: Variance) =
      substitutor.safeSubstitute(topLevelType.unwrap()).let { substitutedType ->
        val candidate = typeApproximator.approximateToSuperType(substitutedType, TypeApproximatorConfiguration.CapturedAndIntegerLiteralsTypesApproximation)
          ?: substitutedType
        if (candidate.isTypeParameter()) { //attempt substitution based on name on type args for same functions
          candidate.substitutedType() ?: candidate
        } else candidate
      }

    private fun UnwrappedType.substitutedType(): UnwrappedType? =
      substitutor.map.keys.find { it.toString() == toString() }?.let {
        val newType = substitutor.map[it]
        newType
      }

  }

  return substitute(TypeSubstitutor.create(wrappedSubstitution))
}

fun ProofCandidate.applyConversion(): ProofCandidate? =
  copy(
    through = through.substituteAndApproximateCapturedTypes2(
      typeSubstitutor,
      TypeApproximator(through.module.builtIns)
    ) as FunctionDescriptor
  )

fun Diagnostic.suppressProvenTypeMismatch(proofs: List<Proof>): Boolean = //TODO this should only go through if the implicit conversion is true
  factory == Errors.TYPE_MISMATCH &&
    safeAs<DiagnosticWithParameters2<KtExpression, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.b
      val superType = diagnosticWithParameters.a
      proofs.subtypingProof(subType, superType) != null
    } == true

fun Diagnostic.suppressUpperboundViolated(proofs: List<Proof>): Boolean =
  factory == Errors.UPPER_BOUND_VIOLATED &&
    safeAs<DiagnosticFactory2<KtTypeReference, KotlinType, KotlinType>>()?.let { factory ->
      factory.cast(this).run {
        //if this is the kind type checker then it will do the right thing otherwise this proceeds as usual with the regular type checker
        proofs.subtypingProof(a, b) != null
      }
    } == true