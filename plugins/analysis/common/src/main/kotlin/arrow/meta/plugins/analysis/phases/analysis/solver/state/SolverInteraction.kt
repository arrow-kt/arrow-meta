package arrow.meta.plugins.analysis.phases.analysis.solver.state

import arrow.meta.plugins.analysis.phases.analysis.solver.RESULT_VAR_NAME
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.check.model.Branch
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentBodyPre
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentCallPost
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentConditions
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentDefaultValues
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentInvariants
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Liskov.notStrongerPostcondition
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Liskov.notWeakerPrecondition
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Unsatisfiability.unsatBodyPost
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Unsatisfiability.unsatCallPre
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorMessages.Unsatisfiability.unsatInvariants
import arrow.meta.plugins.analysis.smt.fieldNames
import arrow.meta.plugins.analysis.smt.substituteVariable
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Model

// SMT SOLVER INTERACTION
// ======================
// these two functions ultimately call the SMT solver,
// and report errors as desired

internal fun SolverState.checkInconsistency(
  message: (unsatCore: List<BooleanFormula>) -> Unit
): Boolean {
  val unsat = prover.isUnsat
  if (unsat) {
    message(prover.unsatCore)
    solverTrace.add("UNSAT! (inconsistent)")
  }
  return unsat
}

internal fun SolverState.addAndCheckConsistency(
  constraints: Iterable<NamedConstraint>,
  context: ResolutionContext,
  message: (unsatCore: List<BooleanFormula>) -> Unit
): Boolean {
  constraints.forEach { addConstraint(it, context) }
  additionalFieldConstraints(constraints, context).forEach { addConstraint(it, context) }
  return checkInconsistency(message)
}

internal fun SolverState.checkImplicationOf(
  constraint: NamedConstraint,
  context: ResolutionContext,
  message: (model: Model) -> Unit
): Boolean = checkImplicationOf(constraint, true, context, message)

internal fun SolverState.checkImplicationOf(
  constraint: NamedConstraint,
  addFieldConstraints: Boolean,
  context: ResolutionContext,
  message: (model: Model) -> Unit
): Boolean = bracket {
  solver.booleans {
    addConstraint(NamedConstraint("!(${constraint.msg})", not(constraint.formula)), context)
  }
  if (addFieldConstraints)
    additionalFieldConstraints(listOf(constraint), context).forEach { addConstraint(it, context) }
  val unsat = prover.isUnsat
  if (!unsat) {
    message(prover.model)
    solverTrace.add("SAT! (not implied)")
  }
  !unsat
}

internal fun SolverState.additionalFieldConstraints(
  formulae: Iterable<NamedConstraint>,
  context: ResolutionContext
): Set<NamedConstraint> =
  solver
    .formulaManager
    .fieldNames(formulae.map { it.formula })
    .flatMap { (fieldName, appliedTo) ->
      val fqName = FqName(fieldName)
      val descriptor = context.descriptorFor(fqName).getOrNull(0)
      val constraints = singleConstraintsFromFqName(fqName)
      if (descriptor != null &&
          constraints != null &&
          constraints.pre.isEmpty() &&
          constraints.post.size == 1
      ) {
        setOf(
          NamedConstraint(
            constraints.post[0].msg,
            solver.substituteVariable(
              constraints.post[0].formula,
              mapOf(RESULT_VAR_NAME to field(descriptor, appliedTo), "this" to appliedTo)
            )
          )
        )
      } else {
        emptySet()
      }
    }
    .toSet()

/** This obtains the *single* constraint related to a particular FqName */
internal fun SolverState.singleConstraintsFromFqName(name: FqName): DeclarationConstraints? =
  callableConstraints[name]?.takeIf { it.size == 1 }?.first()

// PRODUCE ERRORS FROM INTERACTION
// ===============================

internal fun SolverState.checkDefaultValueInconsistency(
  context: ResolutionContext,
  declaration: Declaration
): Boolean =
  solver.run {
    checkInconsistency { unsatCore ->
      val msg = inconsistentDefaultValues(declaration, unsatCore)
      context.handleError(ErrorIds.Inconsistency.InconsistentDefaultValues, declaration, msg)
    }
  }

/**
 * Checks that this [declaration] does not contain logical inconsistencies in its preconditions. For
 * example:
 * - `(x > 0)`
 * - `(x < 0)`
 *
 * If any inconsistencies are found report them through the [context] trace diagnostics
 */
internal fun SolverState.checkPreconditionsInconsistencies(
  constraints: DeclarationConstraints?,
  context: ResolutionContext,
  declaration: Declaration
): Boolean =
  solver.run {
    constraints?.pre?.let {
      addAndCheckConsistency(it, context) { unsatCore ->
        val msg = inconsistentBodyPre(declaration, unsatCore)
        context.handleError(ErrorIds.Inconsistency.InconsistentBodyPre, declaration, msg)
      }
    }
      ?: false // if there are no preconditions, they are consistent
  }

/**
 * Checks that this [declaration] constraints post conditions hold according to the declaration body
 * in the current solver state
 */
internal fun SolverState.checkPostConditionsImplication(
  constraints: DeclarationConstraints?,
  isConstructor: Boolean,
  context: ResolutionContext,
  declaration: Declaration,
  branch: Branch
) {
  solver.run {
    constraints?.post?.forEach { postCondition ->
      checkImplicationOf(postCondition, !isConstructor, context) {
        val msg = unsatBodyPost(declaration, postCondition, branch)
        context.handleError(ErrorIds.Unsatisfiability.UnsatBodyPost, declaration, msg)
      }
    }
  }
}

/** Checks the pre-conditions in [callConstraints] hold for [resolvedCall] */
internal fun SolverState.checkCallPreConditionsImplication(
  callConstraints: DeclarationConstraints?,
  context: ResolutionContext,
  expression: Expression,
  resolvedCall: ResolvedCall,
  branch: Branch
) =
  solver.run {
    callConstraints?.pre?.forEach { callPreCondition ->
      checkImplicationOf(callPreCondition, context) { model ->
        val msg = unsatCallPre(callPreCondition, resolvedCall, branch, model)
        context.handleError(ErrorIds.Unsatisfiability.UnsatCallPre, expression, msg)
      }
    }
  }

/** Checks the post-conditions in [callConstraints] hold for [resolvedCall] */
internal fun SolverState.checkCallPostConditionsInconsistencies(
  callConstraints: DeclarationConstraints?,
  context: ResolutionContext,
  expression: Expression,
  branch: Branch
): Boolean =
  solver.run {
    callConstraints?.post?.let {
      addAndCheckConsistency(it, context) { unsatCore ->
        val msg = inconsistentCallPost(unsatCore, branch)
        context.handleError(ErrorIds.Inconsistency.InconsistentCallPost, expression, msg)
      }
    }
      ?: false
  }

/** Add the [formulae] to the set and checks that it remains consistent */
internal fun SolverState.checkConditionsInconsistencies(
  formulae: List<NamedConstraint>,
  context: ResolutionContext,
  expression: Element,
  branch: Branch,
  reportIfInconsistent: Boolean
): Boolean =
  solver.run {
    addAndCheckConsistency(formulae, context) { unsatCore ->
      if (reportIfInconsistent) {
        val msg = inconsistentConditions(unsatCore, branch)
        context.handleError(ErrorIds.Inconsistency.InconsistentConditions, expression, msg)
      }
    }
  }

internal fun SolverState.checkInvariantConsistency(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element,
  branch: Branch
): Boolean =
  solver.run {
    addAndCheckConsistency(listOf(constraint), context) {
      val msg = inconsistentInvariants(it, branch)
      context.handleError(ErrorIds.Inconsistency.InconsistentInvariants, expression, msg)
    }
  }

internal fun SolverState.checkInvariant(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element,
  branch: Branch
): Boolean =
  solver.run {
    checkImplicationOf(constraint, context) { model ->
      val msg = unsatInvariants(expression, constraint, branch, model)
      context.handleError(ErrorIds.Unsatisfiability.UnsatInvariants, expression, msg)
    }
  }

internal fun SolverState.checkLiskovWeakerPrecondition(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    checkImplicationOf(constraint, context) {
      val msg = notWeakerPrecondition(constraint)
      context.handleError(ErrorIds.Liskov.NotWeakerPrecondition, expression, msg)
    }
  }

internal fun SolverState.checkLiskovStrongerPostcondition(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    checkImplicationOf(constraint, context) {
      val msg = notStrongerPostcondition(constraint)
      context.handleError(ErrorIds.Liskov.NotStrongerPostcondition, expression, msg)
    }
  }
