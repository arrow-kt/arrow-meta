package arrow.meta.plugins.analysis.phases.analysis.solver.collect.model

import org.sosy_lab.java_smt.api.BooleanFormula

data class NamedConstraint(
  val msg: String,
  val formula: BooleanFormula
)
