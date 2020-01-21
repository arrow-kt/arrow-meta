package arrow.meta.plugins.proofs.phases

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.intersection
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
import org.jetbrains.kotlin.types.TypeUtils

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
  val proofType: ProofStrategy
)

fun FunctionDescriptor.isProof(): Boolean =
  annotations.hasAnnotation(ArrowProof)

fun CompilerContext.extending(types: Collection<KotlinType>): List<Proof> =
  types.flatMap { extensionProofs(it, it.constructor.builtIns.nullableAnyType) }

fun CompilerContext.provenIntersection(subType: KotlinType): KotlinType =
  extensionProofs(subType, subType.constructor.builtIns.nullableAnyType)
    .map { it.to }
    .let { subType.intersection(*it.toTypedArray()) }

fun Proof.callables(descriptorNameFilter: (Name) -> Boolean = { true }): List<CallableMemberDescriptor> =
  to.memberScope
    .getContributedDescriptors(nameFilter = descriptorNameFilter)
    .toList()
    .filterIsInstance<CallableMemberDescriptor>()
    .mapNotNull(CallableMemberDescriptor::discardPlatformBaseObjectFakeOverrides)

fun CompilerContext.extensionProof(subType: KotlinType, superType: KotlinType): Proof? =
  extensionProofs(subType, superType).firstOrNull()

fun CompilerContext.extensionProofs(subType: KotlinType, superType: KotlinType): List<Proof> =
  module.proofs.filter { it.proofType == ProofStrategy.Extension }.matchingCandidates(this, subType, superType)

val ModuleDescriptor.proofs: List<Proof>
  get() =
    if (this is ModuleDescriptorImpl) {
      try {
        val cacheValue = proofCache[this]
        when {
          cacheValue != null -> {
            cacheValue.proofs
          }
          else -> initializeProofCache()
        }
      } catch (e: RuntimeException) {
        println("TODO() Detected exception: ${e.printStackTrace()}")
        emptyList<Proof>()
      }
    } else emptyList()