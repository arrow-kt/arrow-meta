package arrow.meta.proofs

import arrow.meta.phases.resolve.ProofVertex
import arrow.meta.phases.resolve.`isSubtypeOf(NewKotlinTypeChecker)`
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.provesWithBaselineTypeChecker
import arrow.meta.phases.resolve.typeArgumentsMap
import arrow.meta.phases.resolve.unwrappedNotNullableType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory2
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.inference.substituteAndApproximateCapturedTypes
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getImportableDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeApproximator
import org.jetbrains.kotlin.types.UnwrappedType
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
  val superType: UnwrappedType
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

fun Proof.extensionCallables(descriptorNameFilter: (Name) -> Boolean): List<CallableMemberDescriptor> =
  if (proofType == ProofStrategy.Extension) {
    to.memberScope
      .getContributedDescriptors(nameFilter = descriptorNameFilter)
      .toList()
      .filterIsInstance<CallableMemberDescriptor>()
      .mapNotNull {
        when (it) {
          is FunctionDescriptor ->
            if (it.kind == CallableMemberDescriptor.Kind.FAKE_OVERRIDE) null
            else it.copy(through.containingDeclaration, it.modality, it.visibility, it.kind, true).newCopyBuilder()
              .setDispatchReceiverParameter(through.dispatchReceiverParameter)
              .setExtensionReceiverParameter(through.extensionReceiverParameter)
              .build()
          else -> it
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
      superType = superType.unwrappedNotNullableType
    )
    val appliedConversion = proof.applyConversion(candidate)
    appliedConversion.provesWithBaselineTypeChecker(subType, superType)
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
    val toArgsMap = to.typeArgumentsMap(superType)
    val allArgsMap =
      fromArgsMap.filter { it.key.type.isTypeParameter() } + toArgsMap.filter { it.key.type.isTypeParameter() }
    return NewTypeSubstitutorByConstructorMap(
      allArgsMap.map {
        it.key.type.constructor to it.value.type.unwrap()
      }.toMap()
    )
  }


fun Collection<Proof>.dump() {
  println("Arrow Type Proofs:\n${toList().joinToString("\n") { (from, to, through) ->
    "$from -> [${through.fqNameSafe}] -> $to:"
  }}")
}

fun FunctionDescriptor.applyConversion(conversionCandidate: ProofCandidate): CallableDescriptor =
  substituteAndApproximateCapturedTypes(
    conversionCandidate.typeSubstitutor,
    TypeApproximator(module.builtIns)
  )

fun Diagnostic.suppressProvenTypeMismatch(proofs: List<Proof>): Boolean = //TODO this should only go through if the implicit conversion is true
  factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH &&
    safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.a
      val superType = diagnosticWithParameters.b
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