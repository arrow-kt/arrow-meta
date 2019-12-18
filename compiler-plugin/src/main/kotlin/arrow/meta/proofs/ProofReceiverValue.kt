package arrow.meta.proofs

import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType

class ProofReceiverValue(private val kotlinType: KotlinType) : ReceiverValue {
  override fun replaceType(p0: KotlinType): ReceiverValue =
    ProofReceiverValue(p0)

  override fun getOriginal(): ReceiverValue = this

  override fun getType(): KotlinType = kotlinType
}