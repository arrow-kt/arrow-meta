package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.continuations.ContSeq
import org.sosy_lab.java_smt.api.BooleanFormula

typealias Branch = List<BooleanFormula>

class CurrentBranch(private val branches: MutableList<BooleanFormula>) {

  fun get(): Branch = branches

  fun add(constraint: BooleanFormula) {
    branches.add(constraint)
  }

  private fun bracket(): ContSeq<Unit> = ContSeq {
    val currentBranches = branches.toTypedArray()
    yield(Unit)
    branches.clear()
    branches.addAll(currentBranches)
  }

  fun introduce(constraint: BooleanFormula): ContSeq<Unit> =
    introduce(listOf(constraint))

  fun introduce(constraints: List<BooleanFormula>): ContSeq<Unit> =
    bracket().map { constraints.forEach { add(it) } }

  companion object {
    fun new(): CurrentBranch = CurrentBranch(mutableListOf())
  }
}
