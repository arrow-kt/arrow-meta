package arrow.meta.plugins.proofs.phases

import arrow.meta.phases.CompilerContext
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
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

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

fun List<Proof>.extending(types: Collection<KotlinType>): List<Proof> =
  extending(*types.toTypedArray())

fun List<Proof>.extending(vararg types: KotlinType): List<Proof> =
  mapNotNull { proof ->
    val include = types.any {
      !it.isTypeParameter() && proof.from.isSubtypeOf(it)
    }
    if (include) {
      proof
    } else null
  }

fun Proof.callables(descriptorNameFilter: (Name) -> Boolean = { true }): List<CallableMemberDescriptor> =
  to.memberScope
    .getContributedDescriptors(nameFilter = descriptorNameFilter)
    .toList()
    .filterIsInstance<CallableMemberDescriptor>()
    .mapNotNull(CallableMemberDescriptor::discardPlatformBaseObjectFakeOverrides)

fun List<Proof>.extensionProof(compilerContext: CompilerContext, subType: KotlinType, superType: KotlinType): Proof? =
  filter { it.proofType == ProofStrategy.Extension }.matchingCandidates(compilerContext, subType, superType).firstOrNull()

val ModuleDescriptor.proofs: List<Proof>
  get() =
    if (this is ModuleDescriptorImpl) {
      try {
        val cacheValue = proofCache[this]
        when {
          cacheValue != null -> {
            cacheValue.proofs
          }
          else -> emptyList()
        }
      } catch (e: RuntimeException) {
        println("TODO() Detected exception: ${e.printStackTrace()}")
        emptyList<Proof>()
      }
    } else emptyList()