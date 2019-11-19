package arrow.meta.proofs

import arrow.meta.phases.resolve.provesWithBaselineTypeChecker
import arrow.meta.phases.resolve.typeArgumentsMap
import arrow.meta.phases.resolve.unwrappedNotNullableType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.inference.substituteAndApproximateCapturedTypes
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeApproximator
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val ArrowProof: FqName =
  FqName("arrow.Proof")

data class Proof(
  val from: KotlinType,
  val to: KotlinType,
  val through: FunctionDescriptor
)

data class ProofCandidate(
  val from: KotlinType,
  val to: KotlinType,
  val subType: UnwrappedType,
  val superType: UnwrappedType
)

fun FunctionDescriptor.isProof(): Boolean =
  annotations.hasAnnotation(ArrowProof)

fun List<Proof>.hasProof(subType: KotlinType, superType: KotlinType): Boolean =
  matchingCandidates(subType, superType).isNotEmpty()

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
      //if this is the kind type checker then it will do the right thing otherwise this proceeds as usual with the regular type checker
      proofs.hasProof(subType, superType)
    } == true