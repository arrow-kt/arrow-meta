package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.smt.Solver
import org.sosy_lab.java_smt.api.BooleanFormula

/**
 * For each variable, we keep two pieces of data:
 * - the name it was declared with
 * - the element it came from
 * - invariants which may have been declared
 */
data class VarInfo
private constructor(
  val name: String,
  val smtName: String,
  val origin: Element,
  val invariant: BooleanFormula? = null
) {
  companion object {
    public operator fun invoke(
      solver: Solver,
      name: String,
      smtName: String,
      origin: Element,
      invariant: BooleanFormula? = null
    ): VarInfo = VarInfo(name, solver.escape(smtName), origin, invariant)

    public fun unsafeCreate(
      name: String,
      smtName: String,
      origin: Element,
      invariant: BooleanFormula? = null
    ): VarInfo = VarInfo(name, smtName, origin, invariant)
  }
}
