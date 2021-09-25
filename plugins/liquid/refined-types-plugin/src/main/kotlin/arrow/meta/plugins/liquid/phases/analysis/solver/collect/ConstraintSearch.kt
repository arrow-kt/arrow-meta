package arrow.meta.plugins.liquid.phases.analysis.solver.collect

import arrow.meta.internal.mapNotNull
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.check.RESULT_VAR_NAME
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.smt.ObjectFormula
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.renameDeclarationConstraints
import arrow.meta.plugins.liquid.smt.substituteObjectVariables


/**
 * Looks up in the solver state previously collected constraints and
 * returns the constraints associated to this [resolvedCall] resulting descriptor if any
 */
internal fun SolverState.constraintsFromSolverState(
  resolvedCall: ResolvedCall
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
 * from which we usually get post-conditions as invariants
 */
internal fun <A> SolverState.overType(
  f: SolverState.(DeclarationDescriptor) -> A?,
  descriptor: ClassDescriptor
): A? = descriptor.unsubstitutedPrimaryConstructor?.let { f(it) }

/**
 * This combinator allows us to use any of the previous
 * functions, but operating on the primary constructor,
 * from which we usually get post-conditions as invariants
 */
internal fun <A> SolverState.overType(
  context: ResolutionContext,
  f: SolverState.(DeclarationDescriptor) -> A?,
  type: Type
): A? = type.descriptor?.let { overType(f, it) }

/**
 * Obtain the invariants associated with
 * a certain type
 */
internal fun SolverState.typeInvariants(
  context: ResolutionContext,
  type: Type,
  resultName: String
): List<NamedConstraint> =
  typeInvariants(context, type, solver.makeObjectVariable(resultName))

internal fun SolverState.typeInvariants(
  context: ResolutionContext,
  type: Type,
  result: ObjectFormula
): List<NamedConstraint> {
  val invariants = overType(context, { constraintsFromSolverState(it) }, type)?.post?.let { constraints ->
    // replace $result by new name
    constraints.map {
      NamedConstraint(
        "${it.msg} (invariant from $type)",
        solver.substituteObjectVariables(it.formula, mapOf(RESULT_VAR_NAME to result)))
    }
  } ?: emptyList()
  val isNotNull = if (!type.isNullable()) {
    listOf(NamedConstraint(
      "$result is not null",
      solver.isNotNull(result)
    ))
  } else {
    emptyList()
  }
  return invariants + isNotNull
}

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
  val fromParams = (constraints.descriptor as? CallableDescriptor)?.valueParameters?.map { it.name.value }
  val toParams = (to as? CallableDescriptor)?.valueParameters?.map { it.name.value }
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
