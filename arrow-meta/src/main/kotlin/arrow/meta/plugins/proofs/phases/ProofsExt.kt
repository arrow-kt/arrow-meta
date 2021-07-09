package arrow.meta.plugins.proofs.phases

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension

fun DeclarationDescriptor.contextualAnnotations(): Set<FqName> =
  annotations.mapNotNull {
    if (it.isGivenContextProof()) it.fqName
    else null
  }.toSet()

fun DeclarationDescriptor.asProof(): Sequence<Proof> =
  when (this) {
    is PropertyDescriptor -> asProof()
    is ClassConstructorDescriptor -> containingDeclaration.asProof()
    is FunctionDescriptor -> asProof()
    is ClassDescriptor -> asProof()
    is FakeCallableDescriptorForObject -> classDescriptor.asProof()
    else -> TODO("asProof: Unsupported proof declaration type: $this")
  }

fun AnnotationDescriptor.isGivenContextProof(): Boolean =
  type.constructor.declarationDescriptor?.annotations?.hasAnnotation(FqName("arrow.Context")) == true

fun ClassDescriptor.asProof(): Sequence<Proof> =
  annotations.asSequence().mapNotNull {
    when {
      it.isGivenContextProof() -> asGivenProof()
      else -> TODO("asProof: Unsupported proof declaration type: $this")
    }
  }

fun PropertyDescriptor.asProof(): Sequence<Proof> =
  annotations.asSequence().mapNotNull {
    when {
      it.isGivenContextProof() -> if (!isExtension) asGivenProof() else null
      else -> TODO("asProof: Unsupported proof declaration type: $this")
    }
  }

fun FunctionDescriptor.asProof(): Sequence<Proof> =
  annotations.asSequence().mapNotNull {
    when {
      it.isGivenContextProof() -> if (!isExtension) asGivenProof() else null
      else -> TODO("asProof: Unsupported proof declaration type: $this")
    }
  }

internal fun ClassDescriptor.asGivenProof(): GivenProof =
  if (kind == ClassKind.OBJECT) ObjectProof(defaultType, this)
  else ClassProof(defaultType, this)

internal fun CallableMemberDescriptor.asGivenProof(): GivenProof? =
  returnType?.let { CallableMemberProof(it, this) }
