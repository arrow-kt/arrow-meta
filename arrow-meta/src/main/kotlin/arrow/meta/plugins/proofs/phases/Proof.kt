package arrow.meta.plugins.proofs.phases

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.types.KotlinType

sealed class Proof(
  open val to: KotlinType,
  open val through: DeclarationDescriptor
) {

  inline fun <A> fold(
    given: GivenProof.() -> A
  ): A =
    when (this) {
      is GivenProof -> given(this)
    }

  abstract fun isContextAmbiguous(other: Proof): Boolean
}

sealed class GivenProof(
  override val to: KotlinType,
  override val through: DeclarationDescriptor
) : Proof(to, through) {
  abstract val callableDescriptor: CallableDescriptor
  val contexts: Set<FqName> get() = through.contextualAnnotations()
  override fun isContextAmbiguous(other: Proof): Boolean =
    other is GivenProof && contexts == other.contexts
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
