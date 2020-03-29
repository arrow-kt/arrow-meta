package arrow.meta.ide.plugins.proofs.psi

import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.ProofStrategy
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.util.IdeDescriptorRenderers
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module

val proofAnnotation: Regex = Regex("@(arrow\\.)?Proof\\((.*)\\)")

private fun KtNamedFunction.isProofOf(strategy: ProofStrategy): Boolean =
  isAnnotatedWith(proofAnnotation) && this.proofTypes().value.any {
    it.text.endsWith(strategy.name)
  }

fun KtNamedFunction.isExtensionProof(): Boolean =
  isProofOf(ProofStrategy.Extension)

fun KtNamedFunction.isNegationProof(): Boolean =
  isProofOf(ProofStrategy.Negation)

fun KtNamedFunction.isRefinementProof(): Boolean =
  isProofOf(ProofStrategy.Refinement)

fun KtNamedFunction.proofTypes(): ScopedList<KtExpression> =
  ScopedList(annotationEntries
    .first { it.text.matches(proofAnnotation) }
    .valueArguments.mapNotNull { it.getArgumentExpression() })

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