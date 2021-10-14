package arrow.meta.plugins.analysis.phases.analysis.solver

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.types.primitiveType

/**
 * Obtain the descriptors which have been overridden by a declaration,
 * if they exist
 */
fun DeclarationDescriptor.overriddenDescriptors(): Collection<DeclarationDescriptor>? =
  when (this) {
    is CallableMemberDescriptor -> this.overriddenDescriptors
    else -> null
  }

internal val DeclarationDescriptor.topMostOverriden: DeclarationDescriptor
  get() = overriddenDescriptors()?.first {
    it.overriddenDescriptors().isNullOrEmpty()
  } ?: this

/**
 * returns true if we have declared something with a @Law
 * or lives inside an object that inherits Laws
 */
fun DeclarationDescriptor.isALaw(): Boolean =
  annotations().hasAnnotation(FqName("arrow.analysis.Law"))

/**
 * returns true if the type inherits Laws
 */
internal fun DeclarationDescriptor?.isLawsType(): Boolean =
  this?.annotations()?.hasAnnotation(FqName("arrow.analysis.Laws")) ?: false

/**
 * check if a descriptor is compatible with other,
 * in the sense that they refer to a function with
 * the same signature
 */
fun DeclarationDescriptor.isCompatibleWith(
  other: DeclarationDescriptor
): Boolean = when {
  this is CallableDescriptor && other is CallableDescriptor -> {
    // we have to ignore the parameters which come from Laws
    val params1 = this.allParameters.filter { param ->
      !param.type.descriptor.isLawsType() &&
        !(this is ConstructorDescriptor && param is ReceiverParameterDescriptor)
    }
    val params2 = other.allParameters.filter { param ->
      !param.type.descriptor.isLawsType() &&
        !(other is ConstructorDescriptor && param is ReceiverParameterDescriptor)
    }
    params1.size == params2.size &&
      params1.zip(params2).all { (p1, p2) ->
        p1.type.isEqualTo(p2.type)
      }
  }
  else -> true
}

/**
 * check if a descriptor is compatible with other,
 * in the sense that the arguments are (possibly)
 * supertypes
 */
fun DeclarationDescriptor.isLooselyCompatibleWith(
  other: DeclarationDescriptor
): Boolean = when {
  this is CallableDescriptor && other is CallableDescriptor -> {
    // we have to ignore the parameters which come from Laws
    val params1 = this.allParameters.filter { param ->
      !param.type.descriptor.isLawsType() &&
        !(this is ConstructorDescriptor && param is ReceiverParameterDescriptor)
    }
    val params2 = other.allParameters.filter { param ->
      !param.type.descriptor.isLawsType() &&
        !(other is ConstructorDescriptor && param is ReceiverParameterDescriptor)
    }
    params1.size == params2.size &&
      params1.zip(params2).all { (p1, p2) ->
        p2.type.isSubtypeOf(p1.type)
      }
  }
  else -> true
}

/**
 * should we treat a node as a field and create 'field(name, x)'?
 */
fun DeclarationDescriptor.isField(): Boolean = when (this) {
  is PropertyDescriptor ->
    hasOneReceiver() && !(returnType?.descriptor?.isFun ?: false)
  is FunctionDescriptor ->
    (name.value.startsWith("is") || name.value.startsWith("get")) && // it's a getter
      valueParameters.isEmpty() && hasOneReceiver() &&
      (returnType?.unwrappedNotNullableType?.primitiveType() != null)
  else -> false
}

/**
 * has a dispatch or extension receiver, but not both
 */
internal fun CallableDescriptor.hasOneReceiver(): Boolean =
  (extensionReceiverParameter != null && dispatchReceiverParameter == null) ||
    (extensionReceiverParameter == null && dispatchReceiverParameter != null)
