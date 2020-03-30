package arrow.meta.plugins.proofs.phases

import arrow.meta.dsl.platform.cli
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import arrow.meta.plugins.proofs.phases.resolve.scopes.discardPlatformBaseObjectFakeOverrides
import arrow.meta.plugins.proofs.phases.resolve.matchingCandidates
import arrow.meta.plugins.proofs.phases.resolve.cache.proofCache
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType

val ArrowProof: FqName =
  FqName("arrow.Proof")

enum class ProofStrategy {
  Extension,
  Refinement,
  Negation
}

data class Proof(
  val from: KotlinType,
  val to: KotlinType,
  val through: FunctionDescriptor,
  val proofType: ProofStrategy,
  val coerce: Boolean
)

fun FunctionDescriptor.isProof(): Boolean =
  annotations.hasAnnotation(ArrowProof)

fun CompilerContext.extending(types: Collection<KotlinType>): List<Proof> =
  types.flatMap { extensionProofs(it, it.constructor.builtIns.nullableAnyType) }

fun Proof.callables(descriptorNameFilter: (Name) -> Boolean = { true }): List<CallableMemberDescriptor> =
  to.memberScope
    .getContributedDescriptors(nameFilter = descriptorNameFilter)
    .toList()
    .filterIsInstance<CallableMemberDescriptor>()
    .mapNotNull(CallableMemberDescriptor::discardPlatformBaseObjectFakeOverrides)

fun CompilerContext.extensionProof(subType: KotlinType, superType: KotlinType): Proof? =
  extensionProofs(subType, superType).firstOrNull()

fun CompilerContext.extensionProofs(subType: KotlinType, superType: KotlinType): List<Proof> =
  module.proofs.filter { it.proofType == ProofStrategy.Extension }
    .matchingCandidates(this, subType, superType)

fun CompilerContext.coerceProof(subType: KotlinType, superType: KotlinType): Proof? =
  coerceProofs(subType, superType).firstOrNull()

fun CompilerContext.coerceProofs(subType: KotlinType, superType: KotlinType): List<Proof> =
  module.proofs.filter { it.coerce }
    .matchingCandidates(this, subType, superType)

fun CompilerContext.areTypesCoerced(subType: KotlinType, supertype: KotlinType): Boolean {
  val isSubtypeOf = baseLineTypeChecker.isSubtypeOf(subType, supertype)

  return if (!isSubtypeOf) {
    val isProofSubtype = ctx.coerceProof(subType, supertype) != null

    !isSubtypeOf && isProofSubtype

  } else false
}

val ModuleDescriptor.proofs: List<Proof>
  get() =
      if (this is ModuleDescriptorImpl) {
        try {
          val cacheValue = proofCache[this]
          when {
            cacheValue != null -> {
              cacheValue.proofs
            }
            else -> cli { initializeProofCache() } ?: emptyList()
          }
        } catch (e: RuntimeException) {
          println("TODO() Detected exception: ${e.printStackTrace()}")
          emptyList<Proof>()
        }
      } else emptyList()
