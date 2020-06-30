package arrow.meta.ide.plugins.proofs.psi

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.proofs
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.isTopLevelKtOrJavaMember
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module

val givenAnnotation: Regex = Regex("@(arrow\\.)?Given")
val extensionAnnotation: Regex = Regex("@(arrow\\.)?Extension")
val coercionAnnotation: Regex = Regex("@(arrow\\.)?Coercion")
val refinementAnnotation: Regex = Regex("@(arrow\\.)?Refinement")

fun KtAnnotated.isGivenProof(): Boolean =
  isTopLevelKtOrJavaMember() && isAnnotatedWith(givenAnnotation)

fun KtParameter.isGivenProof(): Boolean =
  typeReference?.isAnnotatedWith(givenAnnotation) ?: false

fun KtAnnotated.isCoercionProof(): Boolean =
  isTopLevelKtOrJavaMember() && isAnnotatedWith(coercionAnnotation)

fun KtAnnotated.isExtensionProof(): Boolean =
  isTopLevelKtOrJavaMember() && isAnnotatedWith(extensionAnnotation)

fun KtAnnotated.isRefinementProof(): Boolean =
  isTopLevelKtOrJavaMember() && isAnnotatedWith(refinementAnnotation)

fun DeclarationDescriptor.proof(ctx: CompilerContext): Proof? =
  module.proofs(ctx).find {
    it.through.fqNameSafe == fqNameSafe
  }

fun FunctionDescriptor.returnTypeCallableMembers(): List<CallableMemberDescriptor> =
  returnType
    ?.memberScope
    ?.getContributedDescriptors { true }
    ?.filterIsInstance<CallableMemberDescriptor>()
    ?.filter { it.kind != CallableMemberDescriptor.Kind.FAKE_OVERRIDE }
    .orEmpty()