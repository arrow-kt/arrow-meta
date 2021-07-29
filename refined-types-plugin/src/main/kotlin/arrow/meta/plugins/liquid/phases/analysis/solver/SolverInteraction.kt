package arrow.meta.plugins.liquid.phases.analysis.solver

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Model


// SOLVER INTERACTION
// ==================
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