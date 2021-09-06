package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import org.jetbrains.kotlin.psi.KtElement
import org.sosy_lab.java_smt.api.BooleanFormula

/**
 * For each variable, we keep two pieces of data:
 * - the name it was declared with
 * - the element it came from
 * - invariants which may have been declared
 */
data class VarInfo(
  val name: String,
  val smtName: String,
  val origin: KtElement,
  val invariant: BooleanFormula? = null
)
