package arrow.meta.plugins.liquid.phases.analysis.solver.state

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.check.RESULT_VAR_NAME
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentBodyPre
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentCallPost
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentConditions
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Inconsistency.inconsistentInvariants
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Liskov.notStrongerPostcondition
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Liskov.notWeakerPrecondition
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Unsatisfiability.unsatBodyPost
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Unsatisfiability.unsatCallPre
import arrow.meta.plugins.liquid.phases.analysis.solver.errors.ErrorMessages.Unsatisfiability.unsatInvariants
import arrow.meta.plugins.liquid.smt.fieldNames
import arrow.meta.plugins.liquid.smt.substituteVariable
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolvedCall
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Model

// SMT SOLVER INTERACTION
// ======================
// these two functions ultimately call the SMT solver,
// and report errors as desired

internal fun SolverState.addAndCheckConsistency(
  constraints: Iterable<NamedConstraint>,
  message: (unsatCore: List<BooleanFormula>) -> Unit
): Boolean {
  constraints.forEach { addConstraint(it) }
  additionalFieldConstraints(constraints).forEach { addConstraint(it) }
  val unsat = prover.isUnsat
  if (unsat) {
    message(prover.unsatCore)
    solverTrace.add("UNSAT! (inconsistent)")
  }
  return unsat
}

internal fun SolverState.checkImplicationOf(
  constraint: NamedConstraint,
  message: (model: Model) -> Unit
): Boolean =
  bracket {
    solver.booleans { addConstraint(NamedConstraint("!(${constraint.msg})", not(constraint.formula))) }
    additionalFieldConstraints(listOf(constraint)).forEach { addConstraint(it) }
    val unsat = prover.isUnsat
    if (!unsat) {
      message(prover.model)
      solverTrace.add("SAT! (not implied)")
    }
    !unsat
  }

internal fun SolverState.additionalFieldConstraints(
  formulae: Iterable<NamedConstraint>
): Set<NamedConstraint> =
  solver.formulaManager.fieldNames(formulae.map { it.formula })
    .flatMap { (fieldName, appliedTo) ->
      val constraints = constraintsFromFqName(FqName(fieldName))
      if (constraints != null && constraints.pre.isEmpty() && constraints.post.size == 1) {
        setOf(
          NamedConstraint(
            constraints.post[0].msg,
            solver.substituteVariable(
              constraints.post[0].formula,
              mapOf(RESULT_VAR_NAME to solver.field(fieldName, appliedTo), "this" to appliedTo)
            )
          )
        )
      } else {
        emptySet()
      }
    }.toSet()

internal fun SolverState.constraintsFromFqName(name: FqName): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    name == it.descriptor.fqNameSafe
  }

// PRODUCE ERRORS FROM INTERACTION
// ===============================

/**
 * Checks that this [declaration] does not contain logical inconsistencies in its preconditions.
 * For example:
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
      addAndCheckConsistency(it) { unsatCore ->
        val msg = inconsistentBodyPre(declaration, unsatCore)
        context.reportInconsistentBodyPre(declaration, msg)
      }
    } ?: false // if there are no preconditions, they are consistent
  }

/**
 * Checks that this [declaration] constraints post conditions hold
 * according to the declaration body in the current solver state
 */
internal fun SolverState.checkPostConditionsImplication(
  constraints: DeclarationConstraints?,
  context: ResolutionContext,
  declaration: Declaration
) {
  solver.run {
    constraints?.post?.forEach { postCondition ->
      checkImplicationOf(postCondition) {
        val msg = unsatBodyPost(declaration, postCondition)
        context.reportUnsatBodyPost(declaration, msg)
      }
    }
  }
}

/**
 * Checks the pre-conditions in [callConstraints] hold for [resolvedCall]
 */
internal fun SolverState.checkCallPreConditionsImplication(
  callConstraints: DeclarationConstraints?,
  context: ResolutionContext,
  expression: Expression,
  resolvedCall: ResolvedCall
) =
  solver.run {
    callConstraints?.pre?.forEach { callPreCondition ->
      checkImplicationOf(callPreCondition) { model ->
        val msg = unsatCallPre(callPreCondition, resolvedCall, model)
        context.reportUnsatCallPre(expression, msg)
      }
    }
  }

/**
 * Checks the post-conditions in [callConstraints] hold for [resolvedCall]
 */
internal fun SolverState.checkCallPostConditionsInconsistencies(
  callConstraints: DeclarationConstraints?,
  context: ResolutionContext,
  expression: Expression,
  resolvedCall: ResolvedCall
): Boolean =
  solver.run {
    callConstraints?.post?.let {
      addAndCheckConsistency(it) { unsatCore ->
        val msg = inconsistentCallPost(unsatCore)
        context.reportInconsistentCallPost(expression, msg)
      }
    } ?: false
  }

/**
 * Add the [formulae] to the set and checks that it remains consistent
 */
internal fun SolverState.checkConditionsInconsistencies(
  formulae: List<NamedConstraint>,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    addAndCheckConsistency(formulae) { unsatCore ->
      val msg = inconsistentConditions(unsatCore)
      context.reportInconsistentConditions(expression, msg)
    }
  }

internal fun SolverState.checkInvariantConsistency(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    addAndCheckConsistency(listOf(constraint)) {
      val msg = inconsistentInvariants(it)
      context.reportInconsistentInvariants(expression, msg)
    }
  }

internal fun SolverState.checkInvariant(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    checkImplicationOf(constraint) { model ->
      val msg = unsatInvariants(expression, constraint, model)
      context.reportUnsatInvariants(expression, msg)
    }
  }

internal fun SolverState.checkLiskovWeakerPrecondition(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    checkImplicationOf(constraint) { model ->
      val msg = notWeakerPrecondition(constraint)
      context.reportLiskovProblem(expression, msg)
    }
  }

internal fun SolverState.checkLiskovStrongerPostcondition(
  constraint: NamedConstraint,
  context: ResolutionContext,
  expression: Element
): Boolean =
  solver.run {
    checkImplicationOf(constraint) { model ->
      val msg = notStrongerPostcondition(constraint)
      context.reportLiskovProblem(expression, msg)
    }
  }
