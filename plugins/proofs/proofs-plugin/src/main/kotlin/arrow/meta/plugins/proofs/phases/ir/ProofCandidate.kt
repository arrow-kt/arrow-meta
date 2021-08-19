package arrow.meta.plugins.proofs.phases.ir

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType

data class ProofCandidate(
  val proofType: KotlinType,
  val otherType: UnwrappedType,
  val through: DeclarationDescriptor
)
