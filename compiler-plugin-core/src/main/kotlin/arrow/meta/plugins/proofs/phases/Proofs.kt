package arrow.meta.plugins.proofs.phases

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.types.KotlinType

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