package arrow.meta.plugins.proofs.phases

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.diagnostic.ProofRenderer
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import arrow.meta.plugins.proofs.phases.resolve.isResolved
import arrow.meta.plugins.proofs.phases.resolve.matchingCandidates
import arrow.meta.plugins.proofs.phases.resolve.skippedProofsDueToAmbiguities
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val ArrowCompileTime: FqName = FqName("arrow.CompileTime")

fun KtAnnotated.isProof(ctx: BindingContext): Boolean =
  annotations(ctx).any { it.isGivenContextProof() }

fun KtAnnotated.annotations(trace: BindingTrace): List<AnnotationDescriptor> =
  annotationEntries.mapNotNull { trace.get(BindingContext.ANNOTATION, it) }

fun KtAnnotated.annotations(ctx: BindingContext): List<AnnotationDescriptor> =
  annotationEntries.mapNotNull { ctx.get(BindingContext.ANNOTATION, it) }

fun KtAnnotated.hasAnnotation(trace: BindingTrace, fqName: FqName): Boolean =
  annotations(trace).any { it.fqName == fqName }

fun KtAnnotated.hasAnnotation(ctx: BindingContext, fqName: FqName): Boolean =
  annotations(ctx).any { it.fqName == fqName }

fun Annotated.isProof(): Boolean = annotations.any { it.isGivenContextProof() }

private fun CallableMemberDescriptor.discardPlatformBaseObjectFakeOverrides():
  CallableMemberDescriptor? =
  when (kind) {
    CallableMemberDescriptor.Kind.FAKE_OVERRIDE ->
      if (dispatchReceiverParameter?.type == builtIns.anyType) null else this
    else -> this
  }

fun Proof.callables(
  descriptorNameFilter: (Name) -> Boolean = { true }
): List<CallableMemberDescriptor> =
  to.memberScope
    .getContributedDescriptors(nameFilter = descriptorNameFilter)
    .toList()
    .filterIsInstance<CallableMemberDescriptor>()
    .mapNotNull(CallableMemberDescriptor::discardPlatformBaseObjectFakeOverrides)

data class GivenProofResolution(
  val givenProof: GivenProof?,
  val targetType: KotlinType,
  val ambiguousProofs: List<GivenProof>
)

fun CompilerContext.givenProof(context: FqName, superType: KotlinType): GivenProofResolution =
  givenProofCandidate(superType, givenProofs(context, superType))

private fun CompilerContext.givenProofs(context: FqName, superType: KotlinType): List<GivenProof> =
  proof<GivenProof>().filter { context in it.contexts }.matchingCandidates(this, superType)

private fun CompilerContext.givenProofCandidate(
  targetType: KotlinType,
  candidates: List<GivenProof>
): GivenProofResolution {
  val proofs = givenProofs()
  val c =
    candidates.filter {
      it.isResolved(proofs, mutableSetOf()).first &&
        proofs.skippedProofsDueToAmbiguities().firstOrNull { (p, _) -> p == it } == null
    }
  val maybeAmbiguous = c.filter { it.to != targetType && it.to.isSubtypeOf(targetType) }
  val choosenCandidate =
    c.minByOrNull {
      it.through.safeAs<DeclarationDescriptorWithVisibility>()?.visibility !=
        DescriptorVisibilities.INTERNAL
    }

  val ambiguous = if (maybeAmbiguous.size > 1) maybeAmbiguous else emptyList()
  return GivenProofResolution(choosenCandidate, targetType, ambiguous)
}

inline fun <reified P : Proof> CompilerContext.proof(): List<P> =
  module?.proofs(this)?.filterIsInstance<P>()!!

/**
 * returns a Map, where the keys are [KotlinType] and the values are all corresponding proofs
 * without refining the list as it is done in [givenProofs].
 */
fun CompilerContext.allGivenProofs(): Map<KotlinType, List<GivenProof>> =
  proof<GivenProof>()
    .fold(mutableMapOf<KotlinType, List<GivenProof>>()) { acc, proof ->
      val key = proof.to
      acc.apply {
        if (acc.containsKey(key)) acc[key] = acc[key].orEmpty() + proof
        else acc[key] = listOf(proof)
      }
    }
    .toMap()

/** contrary to [allGivenProofs] it refines the List as it is done in [givenProofs] */
fun CompilerContext.givenProofs(): Map<KotlinType, List<GivenProof>> =
  allGivenProofs()
    .mapValues { (type, proofs) -> proofs.matchingCandidates(this, type) }
    .filterValues { it.isNotEmpty() }

@Synchronized
fun ModuleDescriptor.proofs(ctx: CompilerContext): List<Proof> =
  if (this is ModuleDescriptorImpl) {
    try {
      val cacheValue = ctx.proofCache[this]
      when {
        cacheValue != null -> {
          cacheValue.proofs
        }
        else -> {
          initializeProofCache(ctx)
        }
      }
    } catch (e: RuntimeException) {
      println("TODO() Detected exception: ${e.printStackTrace()}")
      emptyList<Proof>()
    }
  } else emptyList()

fun Proof.asString(): String =
  when (this) {
    is GivenProof ->
      "GivenProof ${through.fqNameSafe.asString()} on the type ${ProofRenderer.renderType(to)}"
  }
