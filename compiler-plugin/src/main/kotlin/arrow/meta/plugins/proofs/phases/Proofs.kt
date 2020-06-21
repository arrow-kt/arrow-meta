package arrow.meta.plugins.proofs.phases

import arrow.meta.dsl.platform.cli
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import arrow.meta.plugins.proofs.phases.resolve.cache.proofCache
import arrow.meta.plugins.proofs.phases.resolve.matchingCandidates
import arrow.meta.plugins.proofs.phases.resolve.scopes.discardPlatformBaseObjectFakeOverrides
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.types.KotlinType

val ArrowExtensionProof: FqName = FqName("arrow.Extension")
val ArrowGivenProof: FqName = FqName("arrow.Given")
val ArrowCoercionProof: FqName = FqName("arrow.Coercion")
val ArrowRefinementProof: FqName = FqName("arrow.Refinement")

val ArrowProofSet: Set<FqName> = setOf(
  ArrowExtensionProof,
  ArrowGivenProof,
  ArrowCoercionProof,
  ArrowRefinementProof
)

sealed class Proof(
  open val to: KotlinType,
  open val through: DeclarationDescriptor
) {

//  val underliyingFunctionDescriptor: FunctionDescriptor?
//    get() = when (val f = through) {
//      is FunctionDescriptor -> f
//      is PropertyDescriptor -> f.unwrappedGetMethod ?: TODO("Unsupported $f as @given")
//      is ClassDescriptor -> f.constructors.firstOrNull { it.visibility.isVisibleOutside() }
//      else -> TODO("Unsupported $f as @given")
//    }

  inline fun <A> fold(
    given: GivenProof.() -> A,
    coercion: CoercionProof.() -> A,
    projection: ProjectionProof.() -> A,
    refinement: RefinementProof.() -> A
  ): A =
    when (this) {
      is GivenProof -> given(this)
      is CoercionProof -> coercion(this)
      is ProjectionProof -> projection(this)
      is RefinementProof -> refinement(this)
    }
}

sealed class GivenProof(
  override val to: KotlinType,
  override val through: DeclarationDescriptor
) : Proof(to, through) {
  abstract val callableDescriptor: CallableDescriptor
}

data class ClassProof(
  override val to: KotlinType,
  override val through: ClassDescriptor
) : GivenProof(to, through) {
  override val callableDescriptor: CallableDescriptor
    get() = through.unsubstitutedPrimaryConstructor ?: TODO("no primary constructor for ${through.name}")
}

data class ObjectProof(
  override val to: KotlinType,
  override val through: ClassDescriptor
) : GivenProof(to, through) {
  override val callableDescriptor: CallableDescriptor
    get() = FakeCallableDescriptorForObject(through)
}

data class CallableMemberProof(
  override val to: KotlinType,
  override val through: CallableMemberDescriptor
) : GivenProof(to, through) {
  override val callableDescriptor: CallableDescriptor
    get() = through
}

sealed class ExtensionProof(
  open val from: KotlinType,
  override val to: KotlinType,
  override val through: FunctionDescriptor,
  open val coerce: Boolean = false
) : Proof(to, through)

data class CoercionProof(
  override val from: KotlinType,
  override val to: KotlinType,
  override val through: FunctionDescriptor
) : ExtensionProof(from, to, through, true)

data class ProjectionProof(
  override val from: KotlinType,
  override val to: KotlinType,
  override val through: FunctionDescriptor
) : ExtensionProof(from, to, through, false)

data class RefinementProof(
  val from: KotlinType,
  override val to: KotlinType,
  override val through: CallableMemberDescriptor,
  val coerce: Boolean = true
) : Proof(to, through)

fun KtAnnotated.isProof(trace: BindingTrace): Boolean =
  ArrowProofSet.any { hasAnnotation(trace, it) }

fun KtAnnotated.annotations(trace: BindingTrace): List<AnnotationDescriptor> =
  annotationEntries.mapNotNull { trace.get(BindingContext.ANNOTATION, it) }

fun KtAnnotated.hasAnnotation(trace: BindingTrace, fqName: FqName): Boolean =
  annotations(trace).any { it.fqName == fqName }

fun Annotated.isProof(): Boolean =
  ArrowProofSet.any(annotations::hasAnnotation)

fun CompilerContext.extending(types: Collection<KotlinType>): List<ExtensionProof> =
  types.flatMap { extensionProofs(it, it.constructor.builtIns.nullableAnyType) }

fun Proof.callables(descriptorNameFilter: (Name) -> Boolean = { true }): List<CallableMemberDescriptor> =
  to.memberScope
    .getContributedDescriptors(nameFilter = descriptorNameFilter)
    .toList()
    .filterIsInstance<CallableMemberDescriptor>()
    .mapNotNull(CallableMemberDescriptor::discardPlatformBaseObjectFakeOverrides)

fun CompilerContext.extensionProof(subType: KotlinType, superType: KotlinType): Proof? =
  extensionProofs(subType, superType).firstOrNull()

fun CompilerContext.extensionProofs(subType: KotlinType, superType: KotlinType): List<ExtensionProof> =
  proof<ExtensionProof>().matchingCandidates(this, subType, superType)

fun CompilerContext.givenProofs(superType: KotlinType): List<GivenProof> =
  proof<GivenProof>().matchingCandidates(this, superType)

inline fun <reified P : Proof> CompilerContext.proof(): List<P> =
  module.proofs.filterIsInstance<P>()

/**
 * returns a Map, where the keys are from [KotlinType] -> to [KotlinType] and the values are the corresponding Proofs
 */
fun CompilerContext.extensionProofs(): Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>> =
  proof<ExtensionProof>()
    .fold(mutableMapOf<Pair<KotlinType, KotlinType>, List<ExtensionProof>>()) { acc, proof ->
      val key = proof.from to proof.to
      acc.apply {
        if (acc.containsKey(key)) acc[key] = acc[key].orEmpty() + proof
        else acc[key] = listOf(proof)
      }
    }.toMap()

fun CompilerContext.coerceProof(subType: KotlinType, superType: KotlinType): CoercionProof? =
  coerceProofs(subType, superType).firstOrNull()

fun CompilerContext.coerceProofs(subType: KotlinType, superType: KotlinType): List<CoercionProof> =
  proof<CoercionProof>().filter { it.coerce }.matchingCandidates(this, subType, superType)

fun CompilerContext?.areTypesCoerced(subType: KotlinType, supertype: KotlinType): Boolean =
  !baseLineTypeChecker.isSubtypeOf(subType, supertype) && this?.coerceProof(subType, supertype) != null

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
