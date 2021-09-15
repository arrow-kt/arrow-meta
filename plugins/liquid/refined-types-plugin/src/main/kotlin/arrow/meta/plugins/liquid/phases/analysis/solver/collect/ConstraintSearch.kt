package arrow.meta.plugins.liquid.phases.analysis.solver.collect

import arrow.meta.internal.mapNotNull
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.renameDeclarationConstraints
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * Looks up in the solver state previously collected constraints and
 * returns the constraints associated to this [resolvedCall] resulting descriptor if any
 */
internal fun SolverState.constraintsFromSolverState(
  resolvedCall: ResolvedCall<*>
): DeclarationConstraints? =
  constraintsFromSolverState(resolvedCall.resultingDescriptor)

/**
 * Looks up in the solver state previously collected constraints and
 * returns the constraints associated to this [descriptor],
 * or any of the declaration it has overridden, if any
 */
internal fun SolverState.constraintsFromSolverState(
  descriptor: DeclarationDescriptor
): DeclarationConstraints? =
  immediateConstraintsFromSolverState(descriptor)
    ?: overriddenConstraintsFromSolverState(descriptor)

/**
 * Looks up in the solver state previously collected constraints
 * for the given [descriptor], but not for their possible parents
 */
internal fun SolverState.immediateConstraintsFromSolverState(
  descriptor: DeclarationDescriptor
): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    descriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

/**
 * This combinator allows us to use any of the previous
 * functions, but operating on the primary constructor,
 * from which we usually get postconditions as invariants
 */
internal fun <A> SolverState.overType(
  f: SolverState.(DeclarationDescriptor) -> A?,
  descriptor: ClassDescriptor
): A? = descriptor.unsubstitutedPrimaryConstructor?.let { f(it) }

/**
 * Looks up in the solver state previously collected constraints
 * for every declaration the [descriptor] may have overridden
 */
internal fun SolverState.overriddenConstraintsFromSolverState(
  descriptor: DeclarationDescriptor
): DeclarationConstraints? =
  descriptor.overriddenDescriptors()?.mapNotNull { overriddenDescriptor ->
    constraintsFromSolverState(overriddenDescriptor)
  }?.takeIf {
    it.isNotEmpty()
  }?.map {
    // rename the argument names and similar things
    solver.renameConditions(it, descriptor)
  }?.let { overriddenConstraints ->
    // and finally put all the pre- and post-conditions together
    DeclarationConstraints(
      descriptor,
      overriddenConstraints.flatMap { it.pre },
      overriddenConstraints.flatMap { it.post })
  }

/**
 * Rename the conditions from one descriptor
 * to the names of another
 */
internal fun Solver.renameConditions(
  constraints: DeclarationConstraints,
  to: DeclarationDescriptor
): DeclarationConstraints {
  val fromParams = (constraints.descriptor as? CallableDescriptor)?.valueParameters?.map { it.name.asString() }
  val toParams = (to as? CallableDescriptor)?.valueParameters?.map { it.name.asString() }
  return if (fromParams != null && toParams != null) {
    renameDeclarationConstraints(constraints, fromParams.zip(toParams).toMap())
  } else {
    constraints
  }
}

/**
 * Obtain the descriptors which have been overridden by a declaration,
 * if they exist
 */
private fun DeclarationDescriptor.overriddenDescriptors(): Collection<DeclarationDescriptor>? =
  when (this) {
    is CallableMemberDescriptor -> this.overriddenDescriptors
    else -> null
  }
