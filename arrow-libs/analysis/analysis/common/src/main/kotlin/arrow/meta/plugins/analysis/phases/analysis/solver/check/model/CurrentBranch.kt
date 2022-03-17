package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import org.sosy_lab.java_smt.api.BooleanFormula

typealias Branch = List<BooleanFormula>

class CurrentBranch(private val branches: List<BooleanFormula>) {

  fun get(): Branch = branches

  fun add(constraint: BooleanFormula) = CurrentBranch(branches + constraint)

  fun add(constraints: List<BooleanFormula>) = CurrentBranch(branches + constraints)

  companion object {
    fun new(): CurrentBranch = CurrentBranch(emptyList())
  }
}
