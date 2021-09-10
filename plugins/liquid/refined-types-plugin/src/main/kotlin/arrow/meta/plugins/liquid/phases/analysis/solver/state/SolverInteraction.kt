package arrow.meta.plugins.liquid.phases.analysis.solver.state

import arrow.meta.plugins.liquid.errors.MetaErrors
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
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
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
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
): Boolean =
  solver.run {
    constraints?.pre?.let {
      addAndCheckConsistency(it) { unsatCore ->
        val msg = inconsistentBodyPre(declaration, unsatCore)
        context.trace.report(
          MetaErrors.InconsistentBodyPre.on(declaration.psiOrParent, msg)
        )
      }
    } ?: false // if there are no preconditions, they are consistent
  }

/**
 * Checks that this [declaration] constraints post conditions hold
 * according to the declaration body in the current solver state
 */
internal fun SolverState.checkPostConditionsImplication(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
) {
  solver.run {
    constraints?.post?.forEach { postCondition ->
      checkImplicationOf(postCondition) {
        val msg = unsatBodyPost(declaration, postCondition)
        context.trace.report(
          MetaErrors.UnsatBodyPost.on(declaration.psiOrParent, msg)
        )
      }
    }
  }
}

/**
 * Checks the pre-conditions in [callConstraints] hold for [resolvedCall]
 */
internal fun SolverState.checkCallPreConditionsImplication(
  callConstraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  expression: KtExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>
) =
  solver.run {
    callConstraints?.pre?.forEach { callPreCondition ->
      checkImplicationOf(callPreCondition) { model ->
        val msg = unsatCallPre(callPreCondition, resolvedCall, model)
        context.trace.report(
          MetaErrors.UnsatCallPre.on(expression.psiOrParent, msg)
        )
      }
    }
  }

/**
 * Checks the post-conditions in [callConstraints] hold for [resolvedCall]
 */
internal fun SolverState.checkCallPostConditionsInconsistencies(
  callConstraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  expression: KtExpression,
  resolvedCall: ResolvedCall<out CallableDescriptor>
): Boolean =
  solver.run {
    callConstraints?.post?.let {
      addAndCheckConsistency(it) { unsatCore ->
        val msg = inconsistentCallPost(unsatCore)
        context.trace.report(
          MetaErrors.InconsistentCallPost.on(expression.psiOrParent, msg)
        )
      }
    } ?: false
  }

/**
 * Add the [formulae] to the set and checks that it remains consistent
 */
internal fun SolverState.checkConditionsInconsistencies(
  formulae: List<NamedConstraint>,
  context: DeclarationCheckerContext,
  expression: KtElement
): Boolean =
  solver.run {
    addAndCheckConsistency(formulae) { unsatCore ->
      val msg = inconsistentConditions(unsatCore)
      context.trace.report(
        MetaErrors.InconsistentConditions.on(expression.psiOrParent, msg)
      )
    }
  }

internal fun SolverState.checkInvariantConsistency(
  constraint: NamedConstraint,
  context: DeclarationCheckerContext,
  expression: KtElement
): Boolean =
  solver.run {
    addAndCheckConsistency(listOf(constraint)) {
      val msg = inconsistentInvariants(it)
      context.trace.report(
        MetaErrors.InconsistentInvariants.on(expression.psiOrParent, msg)
      )
    }
  }

internal fun SolverState.checkInvariant(
  constraint: NamedConstraint,
  context: DeclarationCheckerContext,
  expression: KtElement
): Boolean =
  solver.run {
    checkImplicationOf(constraint) { model ->
      val msg = unsatInvariants(expression, constraint, model)
      context.trace.report(
        MetaErrors.UnsatInvariants.on(expression.psiOrParent, msg)
      )
    }
  }

internal fun SolverState.checkLiskovWeakerPrecondition(
  constraint: NamedConstraint,
  context: DeclarationCheckerContext,
  expression: KtElement
): Boolean =
  solver.run {
    checkImplicationOf(constraint) { model ->
      val msg = notWeakerPrecondition(constraint)
      context.trace.report(
        MetaErrors.LiskovProblem.on(expression.psiOrParent, msg)
      )
    }
  }

internal fun SolverState.checkLiskovStrongerPostcondition(
  constraint: NamedConstraint,
  context: DeclarationCheckerContext,
  expression: KtElement
): Boolean =
  solver.run {
    checkImplicationOf(constraint) { model ->
      val msg = notStrongerPostcondition(constraint)
      context.trace.report(
        MetaErrors.LiskovProblem.on(expression.psiOrParent, msg)
      )
    }
  }
