package arrow.meta.ide.plugins.proofs.psi

import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.proofs
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.util.IdeDescriptorRenderers
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module

val givenAnnotation: Regex = Regex("@(arrow\\.)?Given\\((.*)\\)")
val extensionAnnotation: Regex = Regex("@(arrow\\.)?Extension\\((.*)\\)")
val coercionAnnotation: Regex = Regex("@(arrow\\.)?Coercion\\((.*)\\)")
val refinementAnnotation: Regex = Regex("@(arrow\\.)?Refinement\\((.*)\\)")

fun KtNamedFunction.isGivenProof(): Boolean =
  isAnnotatedWith(givenAnnotation)

fun KtNamedFunction.isExtensionProof(): Boolean =
  isAnnotatedWith(extensionAnnotation)

fun KtNamedFunction.isNegationProof(): Boolean =
  isAnnotatedWith(coercionAnnotation)

fun KtNamedFunction.isRefinementProof(): Boolean =
  isAnnotatedWith(refinementAnnotation)

val FunctionDescriptor.from: String
  get() = extensionReceiverParameter?.type?.let(IdeDescriptorRenderers.SOURCE_CODE::renderType).orEmpty()

val FunctionDescriptor.to: String
  get() = returnType?.toString().orEmpty()

fun FunctionDescriptor.proof(): Proof? =
  module.proofs.find { it.through.fqNameSafe == fqNameSafe }

fun FunctionDescriptor.returnTypeCallableMembers(): List<CallableMemberDescriptor> =
  returnType
    ?.memberScope
    ?.getContributedDescriptors { true }
    ?.filterIsInstance<CallableMemberDescriptor>()
    ?.filter { it.kind != CallableMemberDescriptor.Kind.FAKE_OVERRIDE }
    .orEmpty()