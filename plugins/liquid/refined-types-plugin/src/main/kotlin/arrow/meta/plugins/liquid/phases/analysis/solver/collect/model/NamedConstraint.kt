package arrow.meta.plugins.liquid.phases.analysis.solver.collect.model

import org.sosy_lab.java_smt.api.BooleanFormula

data class NamedConstraint(
  val msg: String,
  val formula: BooleanFormula
)
