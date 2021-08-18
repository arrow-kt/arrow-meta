package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.plugins.liquid.errors.MetaErrors
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Model

// SMT SOLVER INTERACTION
// ======================
// these two functions ultimately call the SMT solver,
// and report errors as desired

internal fun SolverState.addAndCheckConsistency(
  constraints: Iterable<BooleanFormula>,
  message: (unsatCore: List<BooleanFormula>) -> Unit
): Boolean {
  constraints.forEach { addConstraint(it) }
  val unsat = prover.isUnsat
  if (unsat) { message(prover.unsatCore) }
  return unsat
}

internal fun SolverState.checkImplicationOf(
  constraint: BooleanFormula,
  message: (model: Model) -> Unit
): Boolean =
  bracket {
    solver.booleans { addConstraint(not(constraint)) }
    val unsat = prover.isUnsat
    if (!unsat) { message(prover.model) }
    !unsat
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
  constraints?.pre?.let {
    addAndCheckConsistency(it) { unsatCore ->
      context.trace.report(
        MetaErrors.InconsistentBodyPre.on(declaration.psiOrParent, declaration, unsatCore)
      )
    }
  } ?: false // if there are no preconditions, they are consistent

/**
 * Checks that this [declaration] constraints post conditions hold
 * according to the declaration body in the current solver state
 */
internal fun SolverState.checkPostConditionsImplication(
  constraints: DeclarationConstraints?,
  context: DeclarationCheckerContext,
  declaration: KtDeclaration
) {
  constraints?.post?.forEach { postCondition ->
    checkImplicationOf(postCondition) {
      context.trace.report(
        MetaErrors.UnsatBodyPost.on(declaration.psiOrParent, declaration, listOf(postCondition))
      )
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
) = callConstraints?.pre?.forEach { callPreCondition ->
  checkImplicationOf(callPreCondition) {
    context.trace.report(
      MetaErrors.UnsatCallPre.on(expression.psiOrParent, resolvedCall, listOf(callPreCondition))
    )
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
  callConstraints?.post?.let {
    addAndCheckConsistency(it) { unsatCore ->
      context.trace.report(
        MetaErrors.InconsistentCallPost.on(expression.psiOrParent, resolvedCall, unsatCore)
      )
    }
  } ?: false

/**
 * Add the [formulae] to the set and checks that it remains consistent
 */
internal fun SolverState.checkConditionsInconsistencies(
  formulae: List<BooleanFormula>,
  context: DeclarationCheckerContext,
  expression: KtElement
): Boolean =
  addAndCheckConsistency(formulae) { unsatCore ->
    context.trace.report(
      MetaErrors.InconsistentConditions.on(expression.psiOrParent, unsatCore)
    )
  }